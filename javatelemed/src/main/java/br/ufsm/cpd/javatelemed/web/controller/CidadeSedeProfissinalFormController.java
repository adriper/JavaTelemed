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
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.service.CidadeService;
import br.ufsm.cpd.javatelemed.persistence.service.EstadoService;
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
@RequestMapping("/admin/profissional/formCidadeSede/{id}")
public class CidadeSedeProfissinalFormController {

    private static final String COMMAND_NAME = "command";
    @Autowired
    private CidadeService cidadeService;
    @Autowired
    private EstadoService estadoService;
    @Autowired
    private ProfissionalService profissionalService;

    @ModelAttribute(COMMAND_NAME)
    protected CidadeSedeCommand getCommand(@PathVariable(name = "id", required = true) final Long idProfissional) {
        final Optional<Profissional> profissional = profissionalService.findById(idProfissional);
        return profissional.map(CidadeSedeCommand::new).orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));
    }

    @GetMapping()
    protected String showForm(ModelMap model) {
        model.addAttribute("estados", estadoService.findComCidadeAtendimento());
        return "profissional/formCidadeSede";
    }

    @PostMapping()
    protected String submit(
            @Valid @ModelAttribute(COMMAND_NAME) final CidadeSedeCommand command,
            final BindingResult result,
            final ModelMap model,
            final RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return showForm(model);
        }
        final Profissional profissional = command.getProfissional();
        profissional.getCidadesSede().add(command.getCidade());
        profissionalService.save(profissional);
        return "redirect:/admin/profissional/view/" + profissional.getId();
    }

    @InitBinder(COMMAND_NAME)
    protected void binder(WebDataBinder binder) {
        binder.registerCustomEditor(Cidade.class, new EntidadeEditor(cidadeService));
    }

    public static final class CidadeSedeCommand {

        private Profissional profissional;
        @NotNull
        private Cidade cidade;

        public CidadeSedeCommand(Profissional profissional) {
            this.profissional = profissional;
        }

        public Profissional getProfissional() {
            return profissional;
        }

        public void setProfissional(Profissional profissional) {
            this.profissional = profissional;
        }

        public Cidade getCidade() {
            return cidade;
        }

        public void setCidade(Cidade cidade) {
            this.cidade = cidade;
        }
    }
}
