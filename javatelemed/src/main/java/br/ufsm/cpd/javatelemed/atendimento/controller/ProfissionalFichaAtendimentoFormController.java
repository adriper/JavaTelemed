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

import br.ufsm.cpd.javatelemed.exception.FichaAtendimentoException;
import br.ufsm.cpd.javatelemed.persistence.model.FichaAtendimento;
import br.ufsm.cpd.javatelemed.persistence.model.NotaAtendimento;
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.model.Sexo;
import br.ufsm.cpd.javatelemed.persistence.service.FichaAtendimentoService;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalService;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Controller
@RequestMapping("/user/atendimento/recebe/{id}")
public class ProfissionalFichaAtendimentoFormController {

    @Autowired
    private FichaAtendimentoService fichaAtendimentoService;
//    @Autowired
//    private EstadoConsultaService estadoConsultaService;
    @Autowired
    private ProfissionalService profissionalService;
//    @Autowired
//    private AtendimentoService atendimentoService;

    @GetMapping
    protected String recebeFicha(@PathVariable("id") final Long idFicha,
            final Authentication authentication,
            final ModelMap modelMap) {
        try {
            Profissional profissional = profissionalService.findById(((Profissional) authentication.getPrincipal()).getId()).orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));
            final FichaAtendimento ficha = fichaAtendimentoService.atendeFicha(idFicha, profissional);
//            ficha = getFicha(idFicha, authentication);
            return showForm(idFicha, modelMap, ficha, profissional, ficha.getNotas(profissional));
        } catch (FichaAtendimentoException ex) {
            modelMap.addAttribute("msg", ex.getMessage());
            return "/atendimento/profissional/fichaIndisponivel";
        }
    }

    private String showForm(final Long idFicha, final ModelMap modelMap, final FichaAtendimento ficha, final Profissional profissional, final Collection<NotaAtendimento> notas) {
        final Collection<FichaAtendimento> outrasFichas = fichaAtendimentoService.findByCpfNotId(ficha.getCpf(), idFicha);
        modelMap.addAttribute("ficha", ficha);
        modelMap.addAttribute("outrasFichas", outrasFichas);
//        modelMap.addAttribute("estadosConsulta", estadoConsultaService.findOrder("descricao"));
        modelMap.addAttribute("podeEditar", true);
        modelMap.addAttribute("descricaoNota", ficha.getEstadoConsulta().getEnviarTermoEsclarecido()
                ? montaTermoLivreEsclarecido(ficha) : "");
        final FichaAtendimento outroSuspeito = (fichaAtendimentoService.findSuspeitoByCpf(ficha.getCpf(), ficha.getId()));
        modelMap.addAttribute("podeEditarSuspeito", outroSuspeito == null);
        modelMap.addAttribute("situacoesSuspeito", FichaAtendimento.SituacaoSuspeito.values());
        modelMap.addAttribute("outroSuspeito", outroSuspeito != null ? outroSuspeito.getSituacaoSuspeito() : null);
        modelMap.addAttribute("podePrescrever", profissional.getTipoProfissional().getPrescricao());
        modelMap.addAttribute("podeVideoconferencia", profissional.getTipoProfissional().getVideoconferencia());
        modelMap.addAttribute("notas", notas);
        return "atendimento/profissional/realizaAtendimento";

    }

    private String montaTermoLivreEsclarecido(final FichaAtendimento ficha) {
        final Profissional profissional = ficha.getResponsavel();
        final Profissional estudante = ficha.getEstudante();
        final StringBuilder builder = new StringBuilder();
        if (estudante != null) {
            builder.append("Olá. Meu nome é ").append(estudante.getNomeSobrenomePrefix()).append(", sou ").append(estudante.getTipoProfissional().getDescricao().toLowerCase())
                    .append(" e irei te acompanhar neste atendimento, sob a supervisão d").append(profissional.getSexo().equals(Sexo.F) ? "a " : "o ").append(profissional.getNomeSobrenomePrefixNumeroRegistro()).append(". ");
        } else {
            builder.append("Olá. Eu sou ").append(profissional.getSexo().equals(Sexo.F) ? "a " : "o ").append(profissional.getNomeSobrenomePrefixNumeroRegistro()).append(" e serei responsável pelo seu atendimento. ");
        }
        return builder.append(ficha.getEstadoConsulta().getTermoEsclarecimento()).toString();
    }
}
