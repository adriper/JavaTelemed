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
package br.ufsm.cpd.javatelemed.web.controller;

import br.ufsm.cpd.javatelemed.password.PasswordChecker;
import br.ufsm.cpd.javatelemed.password.PasswordResult;
import br.ufsm.cpd.javatelemed.password.PasswordValidationRule;
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.service.RecuperacaoSenhaService;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
@Controller
@RequestMapping("/public/redefinirSenha/{token}")
public class TrocaSenhaTokenFormController {

    private static final String COMMAND_NAME = "command";
    @Autowired
    private RecuperacaoSenhaService recuperacaoSenhaService;
    @Autowired
    private PasswordChecker passwordChecker;

    @ModelAttribute(COMMAND_NAME)
    protected TrocaSenhaTokenCommand getCommand(@PathVariable("token") final String token) {
        final Profissional usuario = recuperacaoSenhaService.validaToken(token);
        return new TrocaSenhaTokenCommand(usuario, token);
    }

    @GetMapping()
    protected String showForm(final ModelMap model) {
        model.addAttribute("regrasSenha", passwordChecker.getValidationRules().stream().map(r -> r.getValidationMessage()).collect(Collectors.toList()));
        return "/senha/redefineSenha";
    }

    @PostMapping()
    protected String submit(@Valid @ModelAttribute(COMMAND_NAME) final TrocaSenhaTokenCommand command,
            final BindingResult result, final ModelMap model, final RedirectAttributes redirect) {
        if (result.hasErrors() || !validate(command, result)) {
            return showForm(model);
        }
        recuperacaoSenhaService.alteraSenhaToken(command.getToken(), command.getNovaSenha1());
        final Profissional usuario = command.getUsuario();
        final String nextPage = usuario.getSenhaAtiva() ? "/senha/senhaAlterada" : "/profissional/cadastroConcluido";
        model.addAttribute("usuario", usuario);
        return nextPage;
    }

    private boolean validate(TrocaSenhaTokenCommand command, BindingResult result) {
        final String novaSenha1 = command.getNovaSenha1();
        final String novaSenha2 = command.getNovaSenha2();
        if (!novaSenha1.equals(novaSenha2)) {
            result.rejectValue("novaSenha2", "errors.invalid");
        }
        final Map<String, String> parametros = new HashMap<>();
        parametros.put("cpf", command.getUsuario().getCpf());
        final Map<String, PasswordResult> resultado = passwordChecker.reset().evaluate(novaSenha1, parametros);
        resultado.values().stream()
                .filter(r -> r.isParcial() && r.isNotValid())
                .map(r -> (PasswordValidationRule) r.getRule())
                .forEach(r -> result.rejectValue("novaSenha1", null, r.getValidationError()));
        return !result.hasErrors();
    }

    public static class TrocaSenhaTokenCommand {

        private final Profissional usuario;
        private final String token;
        @NotBlank
        private String novaSenha1;
        @NotBlank
        private String novaSenha2;

        private TrocaSenhaTokenCommand(Profissional usuario, String token) {
            this.usuario = usuario;
            this.token = token;
        }

        public Profissional getUsuario() {
            return usuario;
        }

        public String getToken() {
            return token;
        }

        public String getNovaSenha1() {
            return novaSenha1;
        }

        public void setNovaSenha1(String novaSenha1) {
            this.novaSenha1 = novaSenha1;
        }

        public String getNovaSenha2() {
            return novaSenha2;
        }

        public void setNovaSenha2(String novaSenha2) {
            this.novaSenha2 = novaSenha2;
        }
    }
}
