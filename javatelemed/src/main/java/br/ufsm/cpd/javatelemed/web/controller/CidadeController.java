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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
@Controller
public class CidadeController {

    @Autowired
    private CidadeService cidadeService;
    @Autowired
    private EstadoService estadoService;

    @RequestMapping("/admin/localizacao/cidade/view/{id}")
    public String view(@PathVariable("id") final Long id, final ModelMap model) {
        final Optional<Cidade> cidade = cidadeService.findById(id);
        model.addAttribute("cidade", cidade.orElseThrow(() -> new IllegalArgumentException("Cidade não encontrada")));
        return "localizacao/viewCidade";
    }

    @RequestMapping("/admin/localizacao/cidade/removersede/{id}")
    public String removerDaSede(@PathVariable("id") final Long idSede, @RequestParam("cidade") final Cidade cidade,
            final ModelMap model) {
        final Cidade cidadeSede = cidadeService.findById(idSede).orElseThrow(() -> new IllegalArgumentException("Cidade não encontrada"));
        cidadeSede.getCidadesAtendidas().remove(cidade);
        cidadeService.save(cidadeSede);
        return "redirect:/admin/localizacao/cidade/atendidas/"+cidadeSede.getId();
    }

    @RequestMapping("/admin/localizacao/cidade/lista/")
    protected String lista(@RequestParam(name = "estado", required = false) Estado estado,
            @RequestParam(name = "nome", required = false, defaultValue = "") final String nome,
            final Pageable page, final ModelMap modelMap) {
        if (estado == null) {
            estado = estadoService.findBySigla("RS");
        }
        modelMap.addAttribute("page", cidadeService.findByEstadoNome(estado, '%' + nome.toUpperCase() + '%', page));
        modelMap.addAttribute("estados", estadoService.findAll());
        modelMap.addAttribute("estado", estado);
        return "localizacao/listaCidades";
    }

    @InitBinder
    protected void binder(final WebDataBinder binder) {
        binder.registerCustomEditor(Estado.class, new EntidadeEditor(estadoService));
        binder.registerCustomEditor(Cidade.class, new EntidadeEditor(cidadeService));
    }
}
