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
import br.ufsm.cpd.javatelemed.persistence.service.CidadeService;
import br.ufsm.cpd.javatelemed.persistence.service.EstadoService;
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
@RequestMapping("/admin/localizacao/cidade/atendidas/{id}")
public class CidadeAtendidasFormController {

    private static final String COMMAND_NAME = "command";
    @Autowired
    private CidadeService cidadeService;
    @Autowired
    private EstadoService estadoService;

    @ModelAttribute(COMMAND_NAME)
    protected CidadeAtendidasCommand getCommand(@PathVariable(name = "id", required = true) final Long idCidadeSede) {
        final Cidade cidadeSede = cidadeService.findById(idCidadeSede).orElseThrow(() -> new IllegalArgumentException("Cidade não encontrada"));
        if (!cidadeSede.getAtendimento()) {
            throw new IllegalArgumentException("Cidade não possui atendimento");
        }
        return new CidadeAtendidasCommand(cidadeSede);
    }

    @GetMapping()
    protected String showForm(ModelMap model) {
        model.addAttribute("estados", estadoService.findAll());
        return "localizacao/formCidadeSede";
    }

    @PostMapping()
    protected String submit(
            @Valid @ModelAttribute(COMMAND_NAME) final CidadeAtendidasCommand command,
            final BindingResult result,
            final ModelMap model,
            final RedirectAttributes redirect) {
        if (result.hasErrors() || !validate(command, result)) {
            return showForm(model);
        }
        final Cidade cidadeSede = command.getCidadeSede();
        cidadeSede.getCidadesAtendidas().add(command.getCidade());
        cidadeService.save(cidadeSede);
        return "redirect:/admin/localizacao/cidade/atendidas/" + cidadeSede.getId();
    }

    @InitBinder(COMMAND_NAME)
    protected void binder(WebDataBinder binder) {
        binder.registerCustomEditor(Cidade.class, new EntidadeEditor(cidadeService));
    }

    private boolean validate(CidadeAtendidasCommand command, BindingResult result) {
        final Cidade cidadeSede = command.getCidadeSede();
        final Cidade cidade = command.getCidade();
        if (cidadeSede.equals(cidade)) {
            result.rejectValue("cidade", "errors.invalid");
        }
        if (cidade.getAtendimento()) {
            result.rejectValue("cidade", "errors.invalid");
        }
        return !result.hasErrors();
    }

    public static final class CidadeAtendidasCommand {

        private Cidade cidadeSede;
        @NotNull
        private Cidade cidade;

        public CidadeAtendidasCommand(Cidade cidadeSede) {
            this.cidadeSede = cidadeSede;
        }

        public Cidade getCidadeSede() {
            return cidadeSede;
        }

        public void setCidadeSede(Cidade cidadeSede) {
            this.cidadeSede = cidadeSede;
        }

        public Cidade getCidade() {
            return cidade;
        }

        public void setCidade(Cidade cidade) {
            this.cidade = cidade;
        }
    }
}
