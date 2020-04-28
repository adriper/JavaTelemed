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

import br.ufsm.cpd.javatelemed.commons.collection.CollectionUtils;
import br.ufsm.cpd.javatelemed.exception.FichaAtendimentoException;
import br.ufsm.cpd.javatelemed.persistence.model.Cidade;
import br.ufsm.cpd.javatelemed.persistence.model.EstadoConsulta;
import br.ufsm.cpd.javatelemed.persistence.model.FichaAtendimento;
import br.ufsm.cpd.javatelemed.persistence.model.IndicadorEstado;
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import javax.transaction.Transactional;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.thymeleaf.util.DateUtils;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Transactional
public interface FichaAtendimentoService extends CrudRepository<FichaAtendimento, Long> {

    public static final String ALFABETO_SENHA = "abcdefghjmnpqrstuvxz"; // não colocar a letra i e l para não confundir com o número 1, e nem "O" para não confundir com 0
    public static final int TAMANHO_ALFABETO_SENHA = ALFABETO_SENHA.length();
    public static final int QUANTIDADE_LETRAS_SENHA = 4;
    public static final int QUANTIDADE_NUMEROS_SENHA = 4;

    public Page<EstadoConsulta> findAll(final Pageable pageable);

    @Query("select f from FichaAtendimento f INNER JOIN FETCH f.estadoConsulta e LEFT JOIN FETCH e.indicadores LEFT JOIN FETCH e.proximosEstadosPossiveis proximosEstados LEFT JOIN FETCH proximosEstados.indicadores LEFT JOIN FETCH e.proximoEstadoPaciente proximoEstadoPaciente LEFT JOIN FETCH proximoEstadoPaciente.indicadores indProximoEstadoPaciente where f.protocolo = :protocolo")
    public FichaAtendimento findByProtocolo(@Param("protocolo") final String protocolo);

    @Query("select f from FichaAtendimento f INNER JOIN FETCH f.estadoConsulta e LEFT JOIN FETCH e.indicadores LEFT JOIN FETCH e.proximosEstadosPossiveis proximosEstados LEFT JOIN FETCH proximosEstados.indicadores LEFT JOIN FETCH e.proximoEstadoPaciente proximoEstadoPaciente LEFT JOIN FETCH proximoEstadoPaciente.indicadores indProximoEstadoPaciente where f.salaAtendimento = :sala")
    public FichaAtendimento findBySalaAtendimento(@Param("sala") final String sala);

    @Query("select count (distinct f) from FichaAtendimento f where f.protocolo = :protocolo and f.senha = :senha")
    public Long countByProtocoloSenha(@Param("protocolo") final String protocolo, @Param("senha") final String senha);

    @Transactional
    public default FichaAtendimento saveWithProtocoloSenha(final FichaAtendimento fichaAtendimento) {
        criaProtocoloSenha(fichaAtendimento);
        return save(fichaAtendimento);
    }

    public default void criaProtocoloSenha(FichaAtendimento fichaAtendimento) {
        // Protocolo
        // ano, mês, número aleatório de 0 a 999.999 (verifica se é único)
        final Calendar now = DateUtils.createNow();
        SimpleDateFormat simpleformat = new SimpleDateFormat("yyyyMM");
        String protocolo;
        String salaAtendimento;
        Random rand = new Random(now.getTimeInMillis());
        do {
            protocolo = simpleformat.format(now.getTime());
            int nextInt = rand.nextInt(1000000);
            protocolo += String.valueOf(nextInt);
        } while (findByProtocolo(protocolo) != null);

        final LocalDateTime dataCriacao = LocalDateTime.now();
        do {
            final String conteudo = protocolo + dataCriacao.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            salaAtendimento = DigestUtils.sha256Hex(conteudo).substring(0, FichaAtendimento.MAX_SALA_ATENDIMENTO);
        } while (findBySalaAtendimento(salaAtendimento) != null);
        
        // Cria a senha no formato ABCD 1234
        final StringBuilder builderSenha = new StringBuilder();
        for (int i = 0; i < QUANTIDADE_LETRAS_SENHA; i++) {
            builderSenha.append(ALFABETO_SENHA.charAt(rand.nextInt(TAMANHO_ALFABETO_SENHA)));
        }
        for (int i = 0; i < QUANTIDADE_NUMEROS_SENHA; i++) {
            builderSenha.append(rand.nextInt(9) + 1); // não vamos colocar 0, para não confundirem com a letra O
        }
        fichaAtendimento.setProtocolo(protocolo);
        fichaAtendimento.setSalaAtendimento(salaAtendimento);
        fichaAtendimento.setSenha(builderSenha.toString());
    }

    public default FichaAtendimento atendeFicha(final Long idFicha, final Profissional profissional) throws FichaAtendimentoException {
        FichaAtendimento ficha = findById(idFicha).orElseThrow(() -> new IllegalArgumentException("Ficha não encontrada para o identificador passado"));
        return atendeFicha(ficha, profissional);

    }

    public default FichaAtendimento atendeFicha(final String salaAtendimento,final Profissional profissional) throws FichaAtendimentoException {
        final FichaAtendimento ficha = findBySalaAtendimento(salaAtendimento);
        if (ficha == null){
            throw new IllegalArgumentException("Ficha não encontrada");
        }
        return atendeFicha(ficha, profissional);
        
    }
    public default FichaAtendimento atendeFicha(final String protoclo, final String senha, final Profissional profissional) throws FichaAtendimentoException {
        final FichaAtendimento ficha = findByProtocolo(protoclo);
        if (ficha == null || !ficha.getSenha().equals(senha)) {
            throw new IllegalArgumentException("Ficha não encontrada");
        }
        return atendeFicha(ficha, profissional);
    }

    /**
     * Busca uma ficha para realizar o atendimento
     *
     * @param ficha
     * @param profissional
     * @return
     */
    public default FichaAtendimento atendeFicha(FichaAtendimento ficha, final Profissional profissional) throws FichaAtendimentoException {

        if (!profissional.podeSerResponsavelFicha() && profissional.getPreceptorResponsavel() == null) {
            throw new FichaAtendimentoException(FichaAtendimentoException.TipoAtendeFichaException.RESPONSAVEL);
        }

        // Verifica conclusão do atendimento
        if (ficha.getEstadoConsulta().getAtendimentoFinalizado()) {
            throw new FichaAtendimentoException(FichaAtendimentoException.TipoAtendeFichaException.CONCLUIDO);
        }

        final Profissional responsavel = profissional.getResponsavel();

        // Verifica as cidades de atendimento
        final Cidade cidadeSedeAtendimento = ficha.getCidadeSedeAtendimento();
        final Set<Cidade> cidadesSede = responsavel.getCidadesSede();
        if (CollectionUtils.isEmpty(cidadesSede) || !responsavel.getCidadesSede().contains(cidadeSedeAtendimento)) {
            throw new FichaAtendimentoException(FichaAtendimentoException.TipoAtendeFichaException.CIDADE_SEDE);
        }

        // Verifica estado da consulta
        final EstadoConsulta estadoConsulta = ficha.getEstadoConsulta();
        final Set<EstadoConsulta> estadosConsultaProfissional = profissional.getTipoProfissional().getEstadosConsulta();
        if (CollectionUtils.isEmpty(estadosConsultaProfissional) || !estadosConsultaProfissional.contains(estadoConsulta)) {
            throw new FichaAtendimentoException(FichaAtendimentoException.TipoAtendeFichaException.ESTADO_CONSULTA);
        }

        boolean save = false;
        if (estadoConsulta.getPermiteAlterarProfissional()) {
            ficha.setResponsavel(responsavel);
            ficha.setEstudante(null);
            save = true;
        } else if ((ficha.getResponsavel() != null && !ficha.getResponsavel().equals(responsavel)) // responsáveis são diferentes
                || (profissional.getPreceptorResponsavel() != null && ( // responsáveis são iguais, é um estudante
                (ficha.getEstudante() != null && !ficha.getEstudante().equals(profissional)) // ficha possui um estudante, mas não é o mesmo usuário
                || (ficha.getEstudante() == null)))) { // a ficha não tem estudante -- não vamos deixar que o estudante "roube" o paciente do seu preceptor
            throw new FichaAtendimentoException(FichaAtendimentoException.TipoAtendeFichaException.OUTRO_PROFISSIONAL);
        }else if (ficha.getResponsavel() == null && !estadoConsulta.getPermiteAlterarProfissional()){
            throw new FichaAtendimentoException(FichaAtendimentoException.TipoAtendeFichaException.RESPONSAVEL_NULL_ESTADO_SEM_TROCA);
        }

        // Verificar se deve ir para o próximo estado quando um profissional obtem a ficha
        if (estadoConsulta.proximoEstadoProfissionalAbre()) {
            final EstadoConsulta estado = ficha.getEstadoConsulta().getProximosEstadosPossiveis().iterator().next();
            ficha.setEstadoConsulta(estado);
            save = true;
        }
        if (save) {
            // O profissional atual é um estudante
            if (!profissional.equals(responsavel)) {
                ficha.setEstudante(profissional);
            }
            ficha = save(ficha);
        }
        return ficha;
    }

    @Query("SELECT f FROM FichaAtendimento f WHERE f.cpf = :cpf and f.id <> :id")
    public Collection<FichaAtendimento> findByCpfNotId(@Param("cpf") final String cpf, @Param("id") final Long id);

    @Query("SELECT count(f) FROM FichaAtendimento f WHERE f.cpf = :cpf and f.responsavel = :responsavel")
    public Number countByCpfResponsavel(@Param("cpf") final String cpf, @Param("responsavel") final Profissional responsavel);

    @Query("SELECT count(f) FROM FichaAtendimento f inner join f.estadoConsulta estado inner join estado.indicadores ind WHERE f.cpf = :cpf and f.responsavel = :responsavel and ind <> :indicador")
    public Number countByCpfResponsavelNotIndicadorEstado(@Param("cpf") final String cpf, @Param("responsavel") final Profissional responsavel, @Param("indicador") final IndicadorEstado indicador);

    @Query("SELECT count(f) FROM FichaAtendimento f WHERE f.cpf = :cpf and f.situacaoSuspeito <> 'N'")
    public Number countSuspeitoByCpf(@Param("cpf") final String cpf);

    @Query("SELECT f FROM FichaAtendimento f WHERE f.cpf = :cpf and f.situacaoSuspeito <> 'N' and f.id <> :id")
    public FichaAtendimento findSuspeitoByCpf(@Param("cpf") final String cpf, @Param("id") final Long id);

    @Query("SELECT count (distinct f) FROM FichaAtendimento f WHERE f.cpf = :cpf and "
            + " (f.momentoCriacao >= :interacao or f.momentoUltimaMensagem >= :interacao)")
    public Number countByCpfUltimaInteracao(@Param("cpf") final String cpf, @Param("interacao") final Date interacao);

    public default boolean podeVisualizar(final FichaAtendimento ficha, final Profissional profissional) {
//        // vamos ver se o profissional é o responsável pela ficha, ou se é responsável por outra ficha de uma pessoa com o mesmo CPF dessa passada
//        if (profissional.equals(ficha.getResponsavel()) || profissional.equals(ficha.getEstudante())) {
//            return true;
//        }
        // verifica se está atendendo alguma ficha e se
        if (profissional.isAdmin()) {
            return true;
        }
        return (Long) countByCpfResponsavelNotIndicadorEstado(ficha.getCpf(), profissional.getResponsavel(), IndicadorEstado.ESTADO_FINAL) > 0L;
    }

    @Query("SELECT DISTINCT f FROM FichaAtendimento f WHERE f.cpf = :cpf AND f.email = :email")
    public Collection<FichaAtendimento> findByCpfEmail(@Param("cpf") String cpf, @Param("email") String email);

}
