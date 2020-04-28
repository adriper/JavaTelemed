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

import br.ufsm.cpd.javatelemed.persistence.model.EstadoConsulta;
import br.ufsm.cpd.javatelemed.persistence.service.EstadoConsultaService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Jáder <jader.schmitt@ufsm.br>
 */
@Controller
public class EstadoConsultaController {

    @Autowired
    private EstadoConsultaService estadoConsultaService;

    @RequestMapping("/admin/estado/consulta/lista/")
    public String lista(final ModelMap model, final Pageable pageable) {
        model.addAttribute("page", estadoConsultaService.findAll(pageable));
        return "estado/consulta/lista";
    }

    @RequestMapping("/admin/estado/consulta/view/{id}")
    public String view(@PathVariable("id") final Long id, final ModelMap model) {
        final Optional<EstadoConsulta> estado = estadoConsultaService.findById(id);
        model.addAttribute("estado", estado.orElseThrow(() -> new IllegalArgumentException("Estado consulta não encontrado")));
        return "estado/consulta/view";
    }

    @RequestMapping("/admin/estado/consulta/deleta/{id}")
    public String deleta(@PathVariable("id") final Long id) {
        estadoConsultaService.deleteById(id);
        return "redirect:/admin/estado/consulta/lista/";
    }

}
