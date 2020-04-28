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

import br.ufsm.cpd.javatelemed.persistence.service.TipoProfissionalService;
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
public class TipoProfissionalController {

    @Autowired
    private TipoProfissionalService tipoProfissionalService;

    @RequestMapping("/admin/profissional/listaTipo/")
    public String lista(final ModelMap model) {
        model.addAttribute("tipos", tipoProfissionalService.findAll());
        return "/profissional/listaTipo";
    }

    @RequestMapping("/admin/profissional/deletaTipo/{id}")
    public String deleta(@PathVariable("id") final Long id, final ModelMap model) {
        tipoProfissionalService.deleteById(id);
        return "redirect:/admin/profissional/listaTipo/";
    }
}
