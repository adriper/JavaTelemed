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
import br.ufsm.cpd.javatelemed.persistence.model.Cidade;
import br.ufsm.cpd.javatelemed.persistence.model.Estado;
import br.ufsm.cpd.javatelemed.persistence.service.CidadeService;
import br.ufsm.cpd.javatelemed.persistence.service.EstadoService;
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
@RequestMapping({"/admin/localizacao/cidade/form/", "/admin/localizacao/cidade/form/{id}"})
public class CidadeFormController {

    private static final String COMMAND_NAME = "command";

    @Autowired
    private CidadeService cidadeService;
    @Autowired
    private EstadoService estadoService;

    @ModelAttribute(COMMAND_NAME)
    protected Cidade getCommand(@PathVariable(name = "id", required = false) final Long id) {
        if (id == null) {
            return new Cidade();
        }
        final Optional<Cidade> cidade = cidadeService.findById(id);
        return cidade.orElseThrow(() -> new IllegalArgumentException("Cidade não encontrada"));
    }

    @GetMapping()
    protected String showForm(ModelMap model) {
        model.addAttribute("estados", estadoService.findAll());
        return "localizacao/formCidade";
    }

    @PostMapping()
    protected String submit(
            @Valid @ModelAttribute(COMMAND_NAME) final Cidade cidade,
            final BindingResult result,
            final ModelMap model,
            final RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return showForm(model);
        }
        final Cidade saved = cidadeService.save(cidade);
        return "redirect:/admin/localizacao/cidade/view/" + saved.getId();
    }

    @InitBinder(COMMAND_NAME)
    protected void binder(WebDataBinder binder) {
        binder.registerCustomEditor(Estado.class, new EntidadeEditor(estadoService));
    }
}
