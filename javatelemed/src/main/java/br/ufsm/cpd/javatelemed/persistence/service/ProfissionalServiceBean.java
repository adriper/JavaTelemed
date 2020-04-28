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

import br.ufsm.cpd.javatelemed.mail.Email;
import br.ufsm.cpd.javatelemed.mail.EmailService;
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.model.TipoProfissional;
import br.ufsm.cpd.javatelemed.persistence.model.TokenRecuperacaoSenha;
import br.ufsm.cpd.javatelemed.persistence.model.UserAuthority;
import br.ufsm.cpd.javatelemed.utils.DateUtils;
import io.micrometer.core.instrument.util.StringUtils;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Service
public class ProfissionalServiceBean {

    @Autowired
    private ProfissionalService profissionalService;
    @Autowired
    private EntityManager em;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TokenRecuperacaoSenhaService tokenRecuperacaoSenhaService;
    @Autowired
    private PasswordEncoder encoder;
    @Value("${br.ufsm.cpd.javatelemed.email.novocadastro.assunto}")
    private String assuntoEmailNovoCadastro;

    public Page<Profissional> findByTipoNomeCpfRegistro(final UserAuthority[] papeis, TipoProfissional tipo, final String nome, final String cpf, final String registro, final Pageable page) {
        final StringBuilder builderSelect = new StringBuilder();
        final StringBuilder builderCount = new StringBuilder();
        final StringBuilder builder = new StringBuilder();
        builderSelect.append("Select distinct p FROM Profissional p ");
        builderCount.append("Select count(p) FROM Profissional p ");
        builder.append(" WHERE p.papel IN :papeis ");
        if (tipo != null || StringUtils.isNotBlank(nome) || StringUtils.isNotBlank(cpf) || StringUtils.isNotBlank(registro)) {
            builder.append(" AND ");
            if (tipo != null) {
                builder.append(" p.tipoProfissional = :tipo and ");
            }
            if (StringUtils.isNotBlank(nome)) {
                builder.append("  upper(p.nome || ' ' || p.sobrenome ) like :nome and ");
            }
            if (StringUtils.isNotBlank(cpf)) {
                builder.append("  p.cpf = :cpf and ");
            }
            if (StringUtils.isNotBlank(registro)) {
                builder.append("  p.numeroRegistro = :registro and ");
            }
            builder.append(" 1 = 1");
        }
        builderSelect.append(builder).append(" ORDER BY p.nome, p.sobrenome ");
        builderCount.append(builder);
        final Query querySelect = em.createQuery(builderSelect.toString());
        final Query queryCount = em.createQuery(builderCount.toString());
        final List<UserAuthority> papeisList = Arrays.asList(papeis);
        querySelect.setParameter("papeis", papeisList);
        queryCount.setParameter("papeis", papeisList);

        if (tipo != null) {
            querySelect.setParameter("tipo", tipo);
            queryCount.setParameter("tipo", tipo);
        }
        if (StringUtils.isNotBlank(nome)) {
            querySelect.setParameter("nome", '%' + nome.toUpperCase() + '%');
            queryCount.setParameter("nome", '%' + nome.toUpperCase() + '%');
        }
        if (StringUtils.isNotBlank(cpf)) {
            querySelect.setParameter("cpf", cpf);
            queryCount.setParameter("cpf", cpf);
        }
        if (StringUtils.isNotBlank(registro)) {
            querySelect.setParameter("registro", registro);
            queryCount.setParameter("registro", registro);
        }

        final Long count = (Long) queryCount.getSingleResult();
        final Integer init = page.getPageNumber() * page.getPageSize();
        querySelect.setFirstResult(init);
        querySelect.setMaxResults(page.getPageSize());
        return new PageImpl(querySelect.getResultList(), page, count);
    }

    public Collection<Profissional> findByTipoNomeCpfRegistro(final TipoProfissional tipo, final String nome, final String cpf, final String registro) {
        final StringBuilder builder = new StringBuilder();
        builder.append("Select p FROM Profissional p ");
        if (tipo != null || StringUtils.isNotBlank(nome) || StringUtils.isNotBlank(cpf) || StringUtils.isNotBlank(registro)) {
            builder.append(" WHERE ");
            if (tipo != null) {
                builder.append(" p.tipoProfissional = :tipo and ");
            }
            if (StringUtils.isNotBlank(nome)) {
                builder.append("  upper(p.nome || ' ' || p.sobrenome ) like :nome and ");
            }
            if (StringUtils.isNotBlank(cpf)) {
                builder.append("  p.cpf = :cpf and ");
            }
            if (StringUtils.isNotBlank(registro)) {
                builder.append("  p.numeroRegistro = :registro and ");
            }
            builder.append(" 1 = 1");
        }
        builder.append(" ORDER BY p.nome, p.sobrenome ");
        final Query query = em.createQuery(builder.toString());
        if (tipo != null) {
            query.setParameter("tipo", tipo);
        }
        if (StringUtils.isNotBlank(nome)) {
            query.setParameter("nome", '%' + nome.toUpperCase() + '%');
        }
        if (StringUtils.isNotBlank(cpf)) {
            query.setParameter("cpf", cpf);
        }
        if (StringUtils.isNotBlank(registro)) {
            query.setParameter("registro", registro);
        }
        return query.getResultList();
    }

    @Transactional
    public Profissional cadastrarNovoProfissional(Profissional profissional, final String host) {
        profissional.setSenha(encoder.encode("gT@pAu$ZF%83aw5C#Wm"));
        profissional.setSenhaAtiva(Boolean.FALSE);
        profissional = profissionalService.save(profissional);
        final Email email = new Email(profissional.getEmail(), assuntoEmailNovoCadastro, "cadastroProfissional");
        final LocalDateTime dataCriacao = LocalDateTime.now();
        final TokenRecuperacaoSenha token = tokenRecuperacaoSenhaService.geraToken(profissional, DateUtils.castToDate(dataCriacao.plusDays(2)));
        email.addParametro("token", token);
        email.addParametro("host", host);
        try {
            emailService.sendEmail(email);
        } catch (Exception ex) {
            throw new RuntimeException("Erro enviando e-mail para cadastro de um novo profissional", ex);
        }
        return profissional;
    }

    @Transactional
    public Profissional enviarEmailCadastroProfissional(Profissional profissional, final String host) {
        final Email email = new Email(profissional.getEmail(), assuntoEmailNovoCadastro, "cadastroProfissional");
        final LocalDateTime dataCriacao = LocalDateTime.now();
        final TokenRecuperacaoSenha token = tokenRecuperacaoSenhaService.geraToken(profissional, DateUtils.castToDate(dataCriacao.plusDays(2)));
        email.addParametro("token", token);
        email.addParametro("host", host);
        try {
            emailService.sendEmail(email);
        } catch (Exception ex) {
            throw new RuntimeException("Erro enviando e-mail para cadastro de um novo profissional", ex);
        }
        return profissional;
    }

}
