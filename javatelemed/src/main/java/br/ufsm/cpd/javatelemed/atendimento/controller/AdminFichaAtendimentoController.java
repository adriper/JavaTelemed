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
package br.ufsm.cpd.javatelemed.atendimento.controller;

import br.ufsm.cpd.javatelemed.commons.web.editor.EntidadeEditor;
import br.ufsm.cpd.javatelemed.persistence.model.EstadoConsulta;
import br.ufsm.cpd.javatelemed.persistence.model.FichaAtendimento;
import br.ufsm.cpd.javatelemed.persistence.service.EstadoConsultaService;
import br.ufsm.cpd.javatelemed.persistence.service.FichaAtendimentoService;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Controller
public class AdminFichaAtendimentoController {

    @Autowired
    private FichaAtendimentoService fichaAtendimentoService;
    @Autowired
    private EstadoConsultaService estadoConsultaService;

    @GetMapping("/admin/atendimento/visualiza/{id}")
    protected String visualiza(@PathVariable("id") final Long idFicha, final ModelMap modelMap) {
        final FichaAtendimento ficha = getFicha(idFicha, false);
        modelMap.addAttribute("ficha", ficha);
        final FichaAtendimento outroSuspeito = (fichaAtendimentoService.findSuspeitoByCpf(ficha.getCpf(), ficha.getId()));
        modelMap.addAttribute("outroSuspeito", outroSuspeito != null ? outroSuspeito.getSituacaoSuspeito() : null);
        final Collection<FichaAtendimento> outrasFichas = fichaAtendimentoService.findByCpfNotId(ficha.getCpf(), idFicha);
        modelMap.addAttribute("outrasFichas", outrasFichas);
        return "atendimento/adminView";
    }

    @PostMapping("/admin/atendimento/estado/{id}")
    protected String trocaEstado(@PathVariable("id") final Long idFicha,
            @RequestParam("estado") final EstadoConsulta estado,
            final ModelMap modelMap) {
        final FichaAtendimento ficha = getFicha(idFicha);
        if (ficha.getEstadoConsulta().getProximosEstadosPossiveis().contains(estado)) {
            ficha.setEstadoConsulta(estado);
            fichaAtendimentoService.save(ficha);
            return "redirect:/admin/atendimento/visualiza/{id}";
        } else {
            throw new IllegalArgumentException("O estado passado não pode ser um próximo estado");
        }
    }

    @GetMapping("/admin/atendimento/removeestudante/{id}")
    protected String removeestudante(@PathVariable("id") final Long idFicha,
            final ModelMap modelMap) {
        final FichaAtendimento ficha = getFicha(idFicha);
        ficha.setEstudante(null);
        fichaAtendimentoService.save(ficha);
        return "redirect:/admin/atendimento/visualiza/{id}";
    }

    @GetMapping("/admin/atendimento/removeresponsavel/{id}")
    protected String removeresponsavel(@PathVariable("id") final Long idFicha,
            final ModelMap modelMap) {
        final FichaAtendimento ficha = getFicha(idFicha);
        ficha.setResponsavel(null);
        fichaAtendimentoService.save(ficha);
        return "redirect:/admin/atendimento/visualiza/{id}";
    }

    private FichaAtendimento getFicha(final Long idFicha) {
        return getFicha(idFicha, true);

    }

    private FichaAtendimento getFicha(final Long idFicha, boolean checkFinalzado) {
        final FichaAtendimento ficha = fichaAtendimentoService.findById(idFicha).orElseThrow(() -> new IllegalArgumentException("Ficha não encontrada"));
        if (checkFinalzado && ficha.getEstadoConsulta().getAtendimentoFinalizado()) {
            throw new IllegalArgumentException("Este atendimento já foi finalizado");
        }
        return ficha;

    }

    @InitBinder
    protected void bind(final WebDataBinder binder) {
        binder.registerCustomEditor(EstadoConsulta.class, new EntidadeEditor(estadoConsultaService));
    }

}
