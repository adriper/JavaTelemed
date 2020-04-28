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
import br.ufsm.cpd.javatelemed.persistence.model.IndicadorEstado;
import br.ufsm.cpd.javatelemed.persistence.service.EstadoConsultaService;
import io.micrometer.core.instrument.util.StringUtils;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Jader
 */
@Controller
@RequestMapping({"/admin/estado/consulta/form/", "/admin/estado/consulta/form/{id}"})
public class EstadoConsultaFormController {

    @Autowired
    private EstadoConsultaService estadoConsultaService;
    private static final String COMMAND_NAME = "command";

    @ModelAttribute(COMMAND_NAME)
    protected EstadoConsulta getCommand(@PathVariable(name = "id", required = false) final Long idEstadoConsulta) {
        if (idEstadoConsulta == null) {
            return new EstadoConsulta();
        }
        final Optional<EstadoConsulta> estadoConsulta = estadoConsultaService.findById(idEstadoConsulta);
        return estadoConsulta.orElseThrow(() -> new IllegalArgumentException("Estado consulta não encontrado"));
    }

    @GetMapping()
    protected String showForm(final ModelMap model) {
        model.addAttribute("indicadoresEstado", IndicadorEstado.values());
        model.addAttribute("proximosEstados", estadoConsultaService.findAll());
        return "estado/consulta/form";
    }

    @PostMapping()
    protected String submit(@Valid @ModelAttribute(COMMAND_NAME) final EstadoConsulta estadoConsulta,
            final BindingResult result, final ModelMap model) {
        if (!result.hasErrors()) {
            if (estadoConsulta.getEnviarTermoEsclarecido() && StringUtils.isBlank(estadoConsulta.getTermoEsclarecimento())){
                result.rejectValue("termoEsclarecimento", "errors.required");
            }
        }
        if (result.hasErrors()) {
            return showForm(model);
        }
        estadoConsultaService.save(estadoConsulta);
        return "redirect:/admin/estado/consulta/lista/";
    }

}
