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

import br.ufsm.cpd.javatelemed.commons.collection.CollectionUtils;
import br.ufsm.cpd.javatelemed.persistence.model.Especialidade;
import br.ufsm.cpd.javatelemed.persistence.service.EspecialidadeService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
@Controller
public class EspecialidadeController {

    @Autowired
    private EspecialidadeService especialidadeService;

    @RequestMapping("/admin/especialidade/lista/")
    public String lista(final ModelMap model) {
        model.addAttribute("especialidades", especialidadeService.findAll());
        return "/especialidade/lista";
    }

    @RequestMapping("/admin/especialidade/view/{id}")
    public String view(@PathVariable("id") final Long id, final ModelMap model) {
        final Optional<Especialidade> especialidade = especialidadeService.findById(id);
        model.addAttribute("especialidade", especialidade.orElseThrow(() -> new IllegalArgumentException("Especialidade não encontrada")));
        return "/especialidade/view";
    }

    @RequestMapping("/admin/especialidade/deleta/{id}")
    public String deleta(@PathVariable("id") final Long id, final ModelMap model) {
        final Especialidade especialidade = especialidadeService.findById(id).orElseThrow(() -> new IllegalArgumentException("Especialidade não encontrada"));
        if (CollectionUtils.isNotEmpty(especialidade.getProfissionais())) {
            throw new IllegalArgumentException("Impossível excluir esta especialidade, pois ela possui profissionais vinculados");
        } else {
            especialidadeService.deleteById(id);
        }
        return "redirect:/admin/especialidade/lista/";
    }
}
