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
package br.ufsm.cpd.javatelemed.service;

import br.ufsm.cpd.javatelemed.mail.Email;
import br.ufsm.cpd.javatelemed.mail.EmailService;
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.model.TokenRecuperacaoSenha;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalService;
import br.ufsm.cpd.javatelemed.persistence.service.TokenRecuperacaoSenhaService;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
@Service
public class RecuperacaoSenhaService {

    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenRecuperacaoSenhaService tokenRecuperacaoSenhaService;
    @Autowired
    private ProfissionalService profissionalService;
    @Value("${br.ufsm.cpd.javatelemed.email.recuperasenha.assunto}")
    private String assuntoEmailRecuperaSenha;

    public void recuperarSenha(final Profissional profissional, final String host) {
        final TokenRecuperacaoSenha token = tokenRecuperacaoSenhaService.geraToken(profissional);
        final Email email = new Email(profissional.getEmail(), assuntoEmailRecuperaSenha, "recuperaSenha");
        email.addParametro("token", token);
        email.addParametro("host", host);
        try {
            emailService.sendEmail(email);
        } catch (Exception ex) {
            throw new RuntimeException("Erro enviando e-mail para recuperação de senha", ex);
        }
    }

    @Transactional
    public Profissional alteraSenhaToken(final String token, final String novaSenha) {
        final Profissional usuario = validaToken(token);
        usuario.setSenhaAtiva(Boolean.TRUE);
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        tokenRecuperacaoSenhaService.invalida(token);
        return profissionalService.save(usuario);
    }

    public Profissional validaToken(final String token) {
        final Optional<TokenRecuperacaoSenha> tokenRecuperacaoSenha = tokenRecuperacaoSenhaService.findByToken(token);
        return tokenRecuperacaoSenha
                .filter(t -> t.isValido())
                .map(t -> t.getProfissional())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));
    }
}
