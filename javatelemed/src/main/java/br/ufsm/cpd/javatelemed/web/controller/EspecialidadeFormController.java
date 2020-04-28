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

import br.ufsm.cpd.javatelemed.persistence.model.Especialidade;
import br.ufsm.cpd.javatelemed.persistence.service.EspecialidadeService;
import java.util.Optional;
import javax.validation.Valid;
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
@RequestMapping({"/admin/especialidade/form/", "/admin/especialidade/form/{id}"})
public class EspecialidadeFormController {

    private static final String COMMAND_NAME = "command";
    @Autowired
    private EspecialidadeService especialidadeService;

    @ModelAttribute(COMMAND_NAME)
    protected Especialidade getCommand(@PathVariable(name = "id", required = false) final Long id) {
        if (id == null) {
            return new Especialidade();
        }
        final Optional<Especialidade> especialidade = especialidadeService.findById(id);
        return especialidade.orElseThrow(() -> new IllegalArgumentException("Especialidade não encontrada"));
    }

    @GetMapping()
    protected String showForm(ModelMap model) {
        return "especialidade/form";
    }

    @PostMapping()
    protected String submit(
            @Valid @ModelAttribute(COMMAND_NAME) final Especialidade especialidade,
            final BindingResult result,
            final ModelMap model,
            final RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return showForm(model);
        }
        especialidadeService.save(especialidade);
        return "redirect:/admin/especialidade/lista/";
    }

    @InitBinder(COMMAND_NAME)
    protected void binder(WebDataBinder binder) {
    }
}
