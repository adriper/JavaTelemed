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

import br.ufsm.cpd.javatelemed.commons.web.editor.EntidadeEditor;
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.model.Sexo;
import br.ufsm.cpd.javatelemed.persistence.model.TipoProfissional;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalService;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalServiceBean;
import br.ufsm.cpd.javatelemed.persistence.service.TipoProfissionalService;
import br.ufsm.cpd.javatelemed.utils.TrampaUtils;
import java.text.DateFormat;
import java.util.Date;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Controller
@RequestMapping({"/admin/profissional/form/", "/admin/profissional/form/{id}"})
public class ProfissionalFormController {

    private static final String COMMAND_NAME = "command";

    @Autowired
    private DateFormat dateFormat;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProfissionalService profissionalService;
    @Autowired
    private ProfissionalServiceBean profissionalServiceBean;
    @Autowired
    private TipoProfissionalService tipoProfissionalService;

    @ModelAttribute(COMMAND_NAME)
    protected Profissional getCommand(@PathVariable(name = "id", required = false) final Long idProfissional, final Authentication authentication) {
        if (idProfissional == null) {
            return new Profissional();
        }
        final Profissional profissional = profissionalService.findById(idProfissional).orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));
        Profissional usuario = (Profissional)authentication.getPrincipal();
        if (!ArrayUtils.contains(usuario.getPossiveisPapeisCadastroProfissional(), profissional.getPapel())){
            throw new IllegalArgumentException("Você não pode editar este profissional");
        }
        return profissional;
    }

    @GetMapping()
    protected String showForm(final ModelMap model, final Authentication authentication) {
        model.addAttribute("sexos", Sexo.values());
        model.addAttribute("papeis", ((Profissional) authentication.getPrincipal()).getPossiveisPapeisCadastroProfissional());
        model.addAttribute("tipos", tipoProfissionalService.findAll());
        return "profissional/form";
    }

    @PostMapping()
    protected String submit(@Valid @ModelAttribute(COMMAND_NAME) final Profissional profissional,
            final BindingResult result, final ModelMap model, final Authentication authentication, 
            final HttpServletRequest request,
            final RedirectAttributes redirect) {
        if (result.hasErrors() || !validate(profissional, result)) {
            return showForm(model, authentication);
        }
        final Profissional saved;
        if (profissional.getId() == null) {
            profissional.setAtivo(Boolean.TRUE);
            saved = profissionalServiceBean.cadastrarNovoProfissional(profissional, TrampaUtils.getHost(request));
        } else {
            saved = profissionalService.save(profissional);
        }
        return "redirect:/admin/profissional/view/" + saved.getId();
    }

    @InitBinder(COMMAND_NAME)
    protected void binder(final WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
        binder.registerCustomEditor(TipoProfissional.class, new EntidadeEditor(tipoProfissionalService));
    }

    private boolean validate(final Profissional profissional, final BindingResult result) {
        final Optional<Profissional> pCpf = profissionalService.findByCpf(profissional.getCpf());
        if (pCpf.isPresent() && !pCpf.get().getId().equals(profissional.getId())) {
            result.rejectValue("cpf", "errors.cpf.existente");
        }
        final Optional<Profissional> pEmail = profissionalService.findByEmail(profissional.getEmail());
        if (pEmail.isPresent() && !pEmail.get().getId().equals(profissional.getId())) {
            result.rejectValue("email", "errors.email.existente");
        }
        return !result.hasErrors();
    }

}
