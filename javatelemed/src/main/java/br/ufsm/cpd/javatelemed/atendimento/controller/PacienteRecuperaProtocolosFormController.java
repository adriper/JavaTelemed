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
package br.ufsm.cpd.javatelemed.atendimento.controller;

import br.ufsm.cpd.javatelemed.persistence.service.AtendimentoService;
import br.ufsm.cpd.javatelemed.utils.RecaptchaUtils;
import br.ufsm.cpd.javatelemed.utils.TrampaUtils;
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
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Controller
@RequestMapping("/atendimento/recuperaprotocolos")
public class PacienteRecuperaProtocolosFormController {

    @Autowired
    private AtendimentoService atendimentoService;

    @ModelAttribute("command")
    protected CPFEmailCommand getCommand() {
        return new CPFEmailCommand();
    }

    @GetMapping
    protected String showForm(final ModelMap modelMap) {
        return "atendimento/formRecuperaProtocolos";
    }

    @PostMapping
    protected String submit(@Valid @ModelAttribute("command") final CPFEmailCommand command, final BindingResult errors,
            @RequestParam("g-recaptcha-response") final String tokenUserRecaptcha,
            final HttpServletRequest request,
            final ModelMap modelMap) {
        if (!RecaptchaUtils.recaptcha(tokenUserRecaptcha)) {
            return showForm(modelMap);
        }
        if (errors.hasErrors()) {
            return showForm(modelMap);
        } else {
            atendimentoService.recuperaProtocolos(command.getCpf(), command.getEmail(), TrampaUtils.getHost(request));
            modelMap.addAttribute("cpf", command.getCpf());
            modelMap.addAttribute("email", command.getEmail());
            return "atendimento/successRecuperaProtocolos";
        }
    }

    public static final class CPFEmailCommand {

        @CPF
        @NotBlank
        private String cpf;
        @Email
        @NotBlank
        private String email;

        public CPFEmailCommand() {
        }

        public String getCpf() {
            return cpf;
        }

        public void setCpf(String cpf) {
            this.cpf = cpf;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

    }

}
