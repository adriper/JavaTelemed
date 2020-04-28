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
import br.ufsm.cpd.javatelemed.persistence.model.Especialidade;
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.service.EspecialidadeService;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalService;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
@Controller
@RequestMapping("/admin/profissional/formEspecialidade/{id}")
public class EspecialidadeProfissionalFormController {

    private static final String COMMAND_NAME = "command";
    @Autowired
    private EspecialidadeService especialidadeService;
    @Autowired
    private ProfissionalService profissionalService;

    @ModelAttribute(COMMAND_NAME)
    protected EspecialidadeCommand getCommand(@PathVariable(name = "id", required = true) final Long idProfissional) {
        final Optional<Profissional> profissional = profissionalService.findById(idProfissional);
        return profissional.map(EspecialidadeCommand::new).orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));
    }

    @GetMapping()
    protected String showForm(ModelMap model) {
        model.addAttribute("especialidades", especialidadeService.findAll());
        return "profissional/formEspecialidade";
    }

    @PostMapping()
    protected String submit(
            @Valid @ModelAttribute(COMMAND_NAME) final EspecialidadeCommand command,
            final BindingResult result,
            final ModelMap model,
            final RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return showForm(model);
        }
        final Profissional profissional = command.getProfissional();
        profissional.getEspecialidades().add(command.getEspecialidade());
        profissionalService.save(profissional);
        return "redirect:/admin/profissional/view/" + profissional.getId();
    }

    @InitBinder(COMMAND_NAME)
    protected void binder(WebDataBinder binder) {
        binder.registerCustomEditor(Especialidade.class, new EntidadeEditor(especialidadeService));
    }

    public static final class EspecialidadeCommand {

        private Profissional profissional;
        @NotNull
        private Especialidade especialidade;

        public EspecialidadeCommand(Profissional profissional) {
            this.profissional = profissional;
        }

        public Profissional getProfissional() {
            return profissional;
        }

        public void setProfissional(Profissional profissional) {
            this.profissional = profissional;
        }

        public Especialidade getEspecialidade() {
            return especialidade;
        }

        public void setEspecialidade(Especialidade especialidade) {
            this.especialidade = especialidade;
        }

    }
}
