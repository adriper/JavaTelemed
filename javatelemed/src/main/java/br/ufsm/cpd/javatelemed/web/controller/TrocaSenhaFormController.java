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
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalService;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
@Controller
@RequestMapping("/profissional/trocaSenha/")
public class TrocaSenhaFormController {

    private static final String COMMAND_NAME = "command";

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProfissionalService profissionalService;
    @Autowired
    private PasswordChecker passwordChecker;

    @ModelAttribute(COMMAND_NAME)
    protected TrocaSenhaCommand getCommand() {
        return new TrocaSenhaCommand();
    }

    @GetMapping()
    protected String showForm(final ModelMap model, final Authentication authentication) {
        final Profissional usuario = (Profissional) authentication.getPrincipal();
        model.addAttribute("usuario", usuario);
        model.addAttribute("regrasSenha", passwordChecker.getValidationRules().stream().map(r -> r.getValidationMessage()).collect(Collectors.toList()));
        return "/profissional/trocaSenha";
    }

    @PostMapping()
    protected String submit(@Valid @ModelAttribute(COMMAND_NAME) final TrocaSenhaCommand command,
            final BindingResult result,
            final ModelMap model,
            final RedirectAttributes redirect,
            final Authentication authentication) {
        final Profissional usuario = (Profissional) authentication.getPrincipal();
        if (result.hasErrors() || !validate(command, usuario, result)) {
            return showForm(model, authentication);
        }
        final Profissional profissional = profissionalService.findByCpf(usuario.getCpf()).get();
        profissional.setSenha(passwordEncoder.encode(command.getNovaSenha1()));
        profissionalService.save(profissional);
        model.addAttribute("usuario", usuario);
        return "/profissional/senhaAlterada";
    }

    private boolean validate(TrocaSenhaCommand command, Profissional usuario, BindingResult result) {
        final String senhaAtual = command.getSenhaAtual();
        final String novaSenha1 = command.getNovaSenha1();
        final String novaSenha2 = command.getNovaSenha2();
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            result.rejectValue("senhaAtual", "errors.invalid");
        }
        if (!novaSenha1.equals(novaSenha2)) {
            result.rejectValue("novaSenha2", "errors.invalid");
        }
        final Map<String, String> parametros = new HashMap<>();
        parametros.put("cpf", usuario.getCpf());
        final Map<String, PasswordResult> resultado = passwordChecker.reset().evaluate(novaSenha1, parametros);
        resultado.values().stream()
                .filter(r -> r.isParcial() && r.isNotValid())
                .map(r -> (PasswordValidationRule) r.getRule())
                .forEach(r -> result.rejectValue("novaSenha1", null, r.getValidationError()));
        return !result.hasErrors();
    }

    public static class TrocaSenhaCommand {

        @NotBlank
        private String senhaAtual;
        @NotBlank
//        @Size(min = 8, max = 128)
        private String novaSenha1;
        @NotBlank
        private String novaSenha2;

        public String getSenhaAtual() {
            return senhaAtual;
        }

        public void setSenhaAtual(String senhaAtual) {
            this.senhaAtual = senhaAtual;
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
