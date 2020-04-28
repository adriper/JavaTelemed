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

import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalService;
import br.ufsm.cpd.javatelemed.service.RecuperacaoSenhaService;
import br.ufsm.cpd.javatelemed.utils.RecaptchaUtils;
import br.ufsm.cpd.javatelemed.utils.TrampaUtils;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
@Controller
@RequestMapping("/public/esqueciSenha/")
public class RecuperacaoSenhaFormController {

    private static final String COMMAND_NAME = "command";
    @Autowired
    private ProfissionalService profissionalService;
    @Autowired
    private RecuperacaoSenhaService recuperacaoSenhaService;

    @ModelAttribute(COMMAND_NAME)
    protected RecuperacaoSenhaCommand getCommand() {
        return new RecuperacaoSenhaCommand();
    }

    @GetMapping()
    protected String showForm(final ModelMap model, HttpServletRequest request) {
        return "/senha/geraToken";
    }

    @PostMapping()
    protected String submit(@Valid @ModelAttribute(COMMAND_NAME) final RecuperacaoSenhaCommand command,
            final BindingResult result,
            @RequestParam("g-recaptcha-response") final String tokenUserRecaptcha,
            final ModelMap model, final HttpServletRequest request) {
        if (!RecaptchaUtils.recaptcha(tokenUserRecaptcha)) {
            return showForm(model, request);
        }
        if (result.hasErrors()) {
            return showForm(model, request);
        }
        if (validate(command, result)) {
            final Profissional profissional = profissionalService.findByCpf(command.getCpf()).get();
            recuperacaoSenhaService.recuperarSenha(profissional, TrampaUtils.getHost(request));
        }
        model.addAttribute("email", command.getEmail());
        return "/senha/tokenGerado";
    }

    private boolean validate(RecuperacaoSenhaCommand command, BindingResult result) {
        final Optional<Profissional> profissional = profissionalService.findByCpf(command.getCpf());
        if (profissional.map(p -> !p.getEmail().equals(command.getEmail())).orElse(Boolean.TRUE)) {
            result.reject("errors.invalid.data");
        }
        return !result.hasErrors();
    }

    public static class RecuperacaoSenhaCommand {

        @NotBlank
        @Email
        private String email;
        @CPF
        private String cpf;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCpf() {
            return cpf;
        }

        public void setCpf(String cpf) {
            this.cpf = cpf;
        }
    }
}
