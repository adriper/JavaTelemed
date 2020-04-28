/*
 * This file is part of JavaTelemed.
 *
 * JavaTelemed is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JavaTelemed is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaTelemed.  If not, see <https://www.gnu.org/licenses/>.
 */
package br.ufsm.cpd.javatelemed.persistence.service;

import br.ufsm.cpd.javatelemed.atendimento.controller.PacienteVisualizaFichaAtendimentoFormController.ProtocoloSenhaCommand;
import br.ufsm.cpd.javatelemed.commons.collection.CollectionUtils;
import br.ufsm.cpd.javatelemed.mail.Email;
import br.ufsm.cpd.javatelemed.mail.EmailService;
import br.ufsm.cpd.javatelemed.persistence.model.EstadoConsulta;
import br.ufsm.cpd.javatelemed.persistence.model.FichaAtendimento;
import br.ufsm.cpd.javatelemed.persistence.model.IndicadorEstado;
import br.ufsm.cpd.javatelemed.persistence.model.NotaAtendimento;
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.utils.DateUtils;
import io.micrometer.core.instrument.util.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Service
public class AtendimentoService {

    @Autowired
    private EntityManager em;
    @Autowired
    private FichaAtendimentoService fichaAtendimentoService;
    @Autowired
    private NotaAtendimentoService notaAtendimentoService;
    @Autowired
    private EmailService emailService;
    @Value("${br.ufsm.cpd.javatelemed.email.trocaestado.assunto}")
    private String assuntoEmailTrocaEstado;
    @Value("${br.ufsm.cpd.javatelemed.email.recuperaprotocolo.assunto}")
    private String assuntoEmailRecuperaProtocolo;

    @Transactional
    public NotaAtendimento saveWithNotaProfissional(final FichaAtendimento ficha, final NotaAtendimento nota) {
        nota.setFichaAtendimento(ficha);
        nota.setEstadoConsulta(ficha.getEstadoConsulta());
        final NotaAtendimento notaSalva = notaAtendimentoService.save(nota);
        ficha.setMomentoUltimaMensagem(nota.getMomentoCriacao());
        if (ficha.getEstadoConsulta().getProximoEstadoNotaProfissional()) {
            ficha.setEstadoConsulta(ficha.getEstadoConsulta().getProximosEstadosPossiveis().iterator().next());
        } else if (ficha.getEstadoConsulta().getProximoEstadoPacienteNotaProfissional()) {
            for (final EstadoConsulta estado : ficha.getEstadoConsulta().getProximosEstadosPossiveis()) {
                if (estado.isEstadoPaciente()) {
                    ficha.setEstadoConsulta(estado);
                    break;
                }
            }
        }
        fichaAtendimentoService.save(ficha);
        return notaSalva;
    }

    @Transactional
    public FichaAtendimento saveWithNotaPaciente(final FichaAtendimento ficha, final NotaAtendimento nota) {
        nota.setFichaAtendimento(ficha);
        nota.setEstadoConsulta(ficha.getEstadoConsulta());
        notaAtendimentoService.save(nota);
        ficha.setMomentoUltimaMensagem(nota.getMomentoCriacao());
        if (ficha.getEstadoConsulta().getProximoEstadoPaciente() != null) {
            ficha.setEstadoConsulta(ficha.getEstadoConsulta().getProximoEstadoPaciente());
        }
        return fichaAtendimentoService.save(ficha);
    }
    
    public void setMomentoAcessoPaciente(final FichaAtendimento ficha){
        if (CollectionUtils.isEmpty(ficha.getNotas())){
            return;
        }
        final LocalDateTime now = LocalDateTime.now();
        final Date dateNow = DateUtils.castToDate(now);
        for (final NotaAtendimento nota : ficha.getNotas()){
            if (nota.getMomentoAcessoPaciente() == null && !nota.getIsPaciente()){
                nota.setMomentoAcessoPaciente(dateNow);
            }
        }
        notaAtendimentoService.saveAll(ficha.getNotas());
    }

    /**
     * Busca as fichas disponíveis para um profissional
     *
     * @param pageable
     * @param profissional
     * @param protocolo
     * @param paciente
     * @param cpf
     * @param estado
     * @param dataInicio
     * @param dataFim
     * @param order
     * @return
     */
    public Page<FichaAtendimento> findAllValues(
            final Pageable pageable,
            Profissional profissional,
            final String protocolo,
            final String paciente,
            final String cpf,
            final EstadoConsulta estado,
            final Date dataInicio,
            final Date dataFim,
            final String order) throws Exception {

        final StringBuilder builderSelect = new StringBuilder();
        final StringBuilder builderCount = new StringBuilder();
        final StringBuilder builder = new StringBuilder();
        builderSelect.append("Select f FROM FichaAtendimento f inner join fetch f.estadoConsulta estado inner join fetch estado.indicadores ind where ");
        builderCount.append("Select count (distinct f.id) FROM FichaAtendimento f inner join f.estadoConsulta estado inner join estado.indicadores ind where ");
        if (StringUtils.isNotBlank(protocolo)) {
            builder.append(" f.protocolo = :protocolo and ");
        }
        if (StringUtils.isNotBlank(cpf)) {
            builder.append(" f.cpf = :cpf and ");
        }
        if (StringUtils.isNotBlank(paciente)) {
            builder.append(" f.nome like :paciente and ");
        }
        if (estado != null) {
            builder.append(" f.estadoConsulta = :estado and ");
        }
        if (dataInicio != null) {
            builder.append(" f.momentoCriacao >= :dataInicio and ");
        }
        if (dataFim != null) {
            builder.append(" f.momentoCriacao <= :dataFim and ");
        }
        // ADICIONA RESTRIÇÕES
        boolean isAdmin = profissional.isAdmin();
        if (isAdmin) {
            // Admin pode ver tudo
            builder.append(" 1 = 1 ");
        } else {
            builder.append(" f.estadoConsulta in (:estadosConsulta) and ") // estado da consulta deve permitir o acesso do profissional
                    .append(" ((f.responsavel is null) or "
                            + "( f.responsavel = :responsavel and ind <> :indicadorTermino) or "
                            + "(ind = :indicadorTrocaProfissional )) and ") // ?? VERIFICAR ISSO
                    .append(" f.cidadeSedeAtendimento in (:cidadesSedeProfissional)");

            if (profissional.getPreceptorResponsavel() != null) {
                builder.append(" and ( f.estudante is null or f.estudante = :estudante) ");
            }

//                    .append(" and exists (select 1 from NotaAtendimento nota where nota.fichaAtendimento = f and nota.profissional = :responsavel) ");
        }

        builderSelect.append(builder);
        builderCount.append(builder);

        if (StringUtils.isNotBlank(order)) {
            builderSelect.append(" order by f.").append(order);
            if (order.contains("momento")) {
                builderSelect.append(" desc ");
            }
        }

        final javax.persistence.Query querySelect = em.createQuery(builderSelect.toString());
        final javax.persistence.Query queryCount = em.createQuery(builderCount.toString());
        if (StringUtils.isNotBlank(cpf)) {
            querySelect.setParameter("cpf", cpf);
            queryCount.setParameter("cpf", cpf);
        }
        if (StringUtils.isNotBlank(paciente)) {
            querySelect.setParameter("paciente", paciente);
            queryCount.setParameter("paciente", paciente);
        }
        if (StringUtils.isNotBlank(protocolo)) {
            querySelect.setParameter("protocolo", paciente.toUpperCase());
            queryCount.setParameter("protocolo", paciente.toUpperCase());
        }
        if (estado != null) {
            querySelect.setParameter("estado", estado);
            queryCount.setParameter("estado", estado);
        }
        if (dataInicio != null) {
            querySelect.setParameter("dataInicio", dataInicio);
            queryCount.setParameter("dataInicio", dataInicio);
        }
        if (dataFim != null) {
            querySelect.setParameter("dataFim", dataFim);
            queryCount.setParameter("dataFim", dataFim);
        }
        if (!isAdmin) {

            final Collection<EstadoConsulta> estadosConsulta = profissional.getTipoProfissional().getEstadosConsulta();
            querySelect.setParameter("estadosConsulta", estadosConsulta);
            queryCount.setParameter("estadosConsulta", estadosConsulta);
            if (profissional.getPreceptorResponsavel() != null) {
                querySelect.setParameter("estudante", profissional);
                queryCount.setParameter("estudante", profissional);
                profissional = profissional.getPreceptorResponsavel();
            }
            querySelect.setParameter("responsavel", profissional);
            queryCount.setParameter("responsavel", profissional);
            querySelect.setParameter("cidadesSedeProfissional", profissional.getCidadesSede());
            queryCount.setParameter("cidadesSedeProfissional", profissional.getCidadesSede());

            querySelect.setParameter("indicadorTermino", IndicadorEstado.ESTADO_FINAL);
            queryCount.setParameter("indicadorTermino", IndicadorEstado.ESTADO_FINAL);
            querySelect.setParameter("indicadorTrocaProfissional", IndicadorEstado.ALTERAR_PROFISSIONAL);
            queryCount.setParameter("indicadorTrocaProfissional", IndicadorEstado.ALTERAR_PROFISSIONAL);

        }
        final Long count = (Long) queryCount.getSingleResult();
        final Integer init = pageable.getPageNumber() * pageable.getPageSize();
        querySelect.setFirstResult(init);
        querySelect.setMaxResults(pageable.getPageSize());

        return new PageImpl(querySelect.getResultList(), pageable, count);
    }
    @Transactional
    public NotaAtendimento trocaEstado(final Profissional usuario, final FichaAtendimento ficha, final EstadoConsulta novoEstado, final String host) {
        if (novoEstado.getEnviaEmailPaciente() && StringUtils.isNotBlank(ficha.getEmail())) {
            // envia email para o paciente
            final Email email = new Email(ficha.getEmail(), assuntoEmailTrocaEstado, "estadoAtendimento");
            final Map<String, Object> parametros = new HashMap<String, Object>(4);
            parametros.put("host", host);
            parametros.put("protocolo", ficha.getProtocolo());
            parametros.put("senha", ficha.getSenha());
            parametros.put("nome", ficha.getNome());
            parametros.put("estado", novoEstado.getDescricao());
            email.setParametros(parametros);
            try {
                emailService.sendEmail(email);
            } catch (Exception ex) {
                Logger.getLogger(AtendimentoService.class.getName()).log(Level.SEVERE, "Não foi possível enviar email de alteração de estado para " + ficha.getEmail(), ex);
            }
        }
        ficha.setEstadoConsulta(novoEstado);
        final NotaAtendimento nota = new NotaAtendimento();
        nota.setEstadoConsulta(novoEstado);
        nota.setProfissional(ficha.getResponsavel());
        if (!usuario.equals(ficha.getResponsavel())){
            nota.setEstudante(usuario);
        }
        nota.setDescricao("Estado do atendimento foi alterado para " + novoEstado.getDescricao());
        nota.setMomentoCriacao(DateUtils.castToDate(LocalDateTime.now()));
        FichaAtendimento save = fichaAtendimentoService.save(ficha);
        nota.setFichaAtendimento(save);
        return notaAtendimentoService.save(nota);
    }

    public void recuperaProtocolos(String cpf, String email, final String host) {
        final Collection<FichaAtendimento> fichas = fichaAtendimentoService.findByCpfEmail(cpf, email);
        if (CollectionUtils.isNotEmpty(fichas)) {
            final Collection<ProtocoloSenhaCommand> protocolosSenhas = new ArrayList<ProtocoloSenhaCommand>(fichas.size());
            for (final FichaAtendimento ficha : fichas) {
                protocolosSenhas.add(new ProtocoloSenhaCommand(ficha.getProtocolo(), ficha.getSenha()));
            }
            final Map<String, Object> map = new HashMap<>();
            map.put("protocolosSenhas", protocolosSenhas);
            map.put("host", host);
            map.put("nome", fichas.iterator().next().getNome());
            final Email emailToSenhd = new Email(email, assuntoEmailRecuperaProtocolo, "recuperaProtocolos");
            emailToSenhd.setParametros(map);
            try {
                emailService.sendEmail(emailToSenhd);
            } catch (Exception ex) {
                Logger.getLogger(AtendimentoService.class.getName()).log(Level.SEVERE, "Erro ao enviar email com protocolos e senha para " + email, ex);
            }
        }
    }

    public Page<FichaAtendimento> findSuspeitosSemRetorno(
            final Pageable pageable,
            Profissional profissional,
            final String protocolo,
            final String paciente,
            final String cpf,
            final EstadoConsulta estado,
            final Date dataInicio,
            final Date dataFim,
            final String order,
            final FichaAtendimento.SituacaoSuspeito situacaoSuspeito) {
        final StringBuilder builderSelect = new StringBuilder();
        final StringBuilder builderCount = new StringBuilder();
        final StringBuilder builder = new StringBuilder();
        builderSelect.append("Select distinct f FROM FichaAtendimento f inner join fetch f.estadoConsulta estado inner join fetch estado.indicadores ind ");
        builderCount.append("Select count (distinct f.id) FROM FichaAtendimento f inner join f.estadoConsulta estado inner join estado.indicadores ind ");
        builder.append(" where f.situacaoSuspeito = :situacaoSuspeito and f.momentoUltimaMensagem < :dataRetorno and not exists "
                + "(select 1 from FichaAtendimento retorno where retorno.cpf = f.cpf and (retorno.momentoCriacao >= :dataRetorno or "
                + " retorno.momentoUltimaMensagem >= :dataRetorno)) and ");
        if (StringUtils.isNotBlank(protocolo)) {
            builder.append(" f.protocolo = :protocolo and ");
        }
        if (StringUtils.isNotBlank(cpf)) {
            builder.append(" f.cpf = :cpf and ");
        }
        if (StringUtils.isNotBlank(paciente)) {
            builder.append(" upper(f.nome) like :paciente and ");
        }
        if (estado != null) {
            builder.append(" f.estadoConsulta = :estado and ");
        }
        if (dataInicio != null) {
            builder.append(" f.momentoCriacao >= :dataInicio and ");
        }
        if (dataFim != null) {
            builder.append(" f.momentoCriacao <= :dataFim and ");
        }
        // ADICIONA RESTRIÇÕES
        boolean isAdmin = profissional.isAdmin();
        if (isAdmin) {
            // Admin pode ver tudo
            builder.append(" 1 = 1 ");
        } else {
            builder.append(" f.cidadeSedeAtendimento in (:cidadesSedeProfissional)");
        }

        builderSelect.append(builder);
        builderCount.append(builder);

        if (StringUtils.isNotBlank(order)) {
            builderSelect.append(" order by f.").append(order);
            if (order.contains("momento")) {
                builderSelect.append(" desc ");
            }
        }

        final javax.persistence.Query querySelect = em.createQuery(builderSelect.toString());
        final javax.persistence.Query queryCount = em.createQuery(builderCount.toString());

        LocalDateTime data = LocalDateTime.now();
        data = data.plusHours(-1 * situacaoSuspeito.getHorasRetorno());
        final Date dataRetorno = DateUtils.castToDate(data);
        querySelect.setParameter("dataRetorno", dataRetorno);
        queryCount.setParameter("dataRetorno", dataRetorno);
        querySelect.setParameter("situacaoSuspeito", situacaoSuspeito);
        queryCount.setParameter("situacaoSuspeito", situacaoSuspeito);

        if (StringUtils.isNotBlank(cpf)) {
            querySelect.setParameter("cpf", cpf);
            queryCount.setParameter("cpf", cpf);
        }
        if (StringUtils.isNotBlank(paciente)) {
            querySelect.setParameter("paciente", '%' + paciente.toUpperCase() + '%');
            queryCount.setParameter("paciente", '%' + paciente.toUpperCase() + '%');
        }
        if (StringUtils.isNotBlank(protocolo)) {
            querySelect.setParameter("protocolo", paciente.toUpperCase());
            queryCount.setParameter("protocolo", paciente.toUpperCase());
        }
        if (estado != null) {
            querySelect.setParameter("estado", estado);
            queryCount.setParameter("estado", estado);
        }
        if (dataInicio != null) {
            querySelect.setParameter("dataInicio", dataInicio);
            queryCount.setParameter("dataInicio", dataInicio);
        }
        if (dataFim != null) {
            querySelect.setParameter("dataFim", dataFim);
            queryCount.setParameter("dataFim", dataFim);
        }
        if (!isAdmin) {
            querySelect.setParameter("cidadesSedeProfissional", profissional.getCidadesSede());
            queryCount.setParameter("cidadesSedeProfissional", profissional.getCidadesSede());
        }
        final Long count = (Long) queryCount.getSingleResult();
        final Integer init = pageable.getPageNumber() * pageable.getPageSize();
        querySelect.setFirstResult(init);
        querySelect.setMaxResults(pageable.getPageSize());
        return new PageImpl(querySelect.getResultList(), pageable, count);
    }
}
