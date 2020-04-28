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

import br.ufsm.cpd.javatelemed.persistence.service.FichaAtendimentoService;
import br.ufsm.cpd.javatelemed.utils.RecaptchaUtils;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Controller
@RequestMapping("/visualizar")
public class PacienteVisualizaFichaAtendimentoFormController {

    @Autowired
    private FichaAtendimentoService fichaAtendimentoService;

    @ModelAttribute("command")
    protected ProtocoloSenhaCommand protocoloSenha() {
        return new ProtocoloSenhaCommand();
    }

    @GetMapping
    protected String showForm(final ModelMap modelMap) {
        return "atendimento/formProtocoloSenha";
    }

    @PostMapping
    protected String submit(@Valid @ModelAttribute("command") final ProtocoloSenhaCommand command, final BindingResult errors,
            @RequestParam("g-recaptcha-response") final String tokenUserRecaptcha,
            final ModelMap modelMap,
            final RedirectAttributes redirect) {
        if (!RecaptchaUtils.recaptcha(tokenUserRecaptcha)) {
            return showForm(modelMap);
        }
        if (errors.hasErrors()) {
            return "atendimento/formProtocoloSenha";
        } else // procura
        {
            if (fichaAtendimentoService.countByProtocoloSenha(command.getProtocolo(), command.getSenha()) == 0L) {
                errors.rejectValue("protocolo", "errors.invalid");
                errors.rejectValue("senha", "errors.invalid");
                return "atendimento/formProtocoloSenha";
            } else {
                redirect.addAttribute("protocolo", command.getProtocolo());
                redirect.addAttribute("senha", command.getSenha());
                return "redirect:/atendimento/visualizar";
            }
        }
    }

    public static class ProtocoloSenhaCommand {

        @NotBlank
        private String protocolo;
        @NotBlank
        private String senha;

        public ProtocoloSenhaCommand(String protocolo, String senha) {
            this.protocolo = protocolo;
            this.senha = senha;
        }

        public String getProtocolo() {
            return protocolo;
        }

        public void setProtocolo(String protocolo) {
            this.protocolo = protocolo;
        }

        public String getSenha() {
            return senha;
        }

        public void setSenha(String senha) {
            this.senha = senha;
        }

        public ProtocoloSenhaCommand() {
        }

    }

}
