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

import br.ufsm.cpd.javatelemed.persistence.model.EstadoConsulta;
import br.ufsm.cpd.javatelemed.persistence.model.FichaAtendimento;
import br.ufsm.cpd.javatelemed.persistence.model.IndicadorEstado;
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.service.AtendimentoService;
import br.ufsm.cpd.javatelemed.persistence.service.EstadoConsultaService;
import br.ufsm.cpd.javatelemed.persistence.service.FichaAtendimentoService;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalService;
import br.ufsm.cpd.javatelemed.utils.DateUtils;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Controller
public class AcompanhaSuspeitoController {

    @Autowired
    private AtendimentoService atendimentoService;
    @Autowired
    private EstadoConsultaService estadoConsultaService;
    @Autowired
    private FichaAtendimentoService fichaAtendimentoService;
    @Autowired
    private ProfissionalService profissionalService;
    @Autowired
    @Qualifier("dateFormat")
    private DateFormat dateFormat;

    @GetMapping("/user/suspeitos/lista/")
    public String listaFichas(final ModelMap model, final Authentication authentication, @PageableDefault final Pageable pageable,
            @RequestParam(name = "order", required = false, defaultValue = "momentoCriacao") final String order,
            @RequestParam(name = "protocolo", required = false) final String protocolo,
            @RequestParam(name = "paciente", required = false) final String paciente,
            @RequestParam(name = "cpf", required = false) final String cpf,
            @RequestParam(name = "dataInicio", required = false) final Date dataInicio,
            @RequestParam(name = "dataFim", required = false) final Date dataFim,
            @RequestParam(name = "estadoConsulta", required = false) final EstadoConsulta estado,
            @RequestParam(name = "situacaoSuspeito") final FichaAtendimento.SituacaoSuspeito situacaoSuspeito) {
        if (situacaoSuspeito == FichaAtendimento.SituacaoSuspeito.N){
            throw new IllegalArgumentException("Situação inválida");
        }
        Profissional profissional = (Profissional) (authentication.getPrincipal());
        profissional = profissionalService.findById(profissional.getId()).orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));
        model.addAttribute("page", atendimentoService.findSuspeitosSemRetorno(pageable, profissional, protocolo, paciente, cpf, estado, dataInicio, dataFim, order, situacaoSuspeito));
        model.addAttribute("order", order);
        model.addAttribute("protocolo", protocolo);
        model.addAttribute("paciente", paciente);
        model.addAttribute("cpf", cpf);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("estadoConsulta", estado);
        model.addAttribute("situacoesSuspeito", new FichaAtendimento.SituacaoSuspeito[]{FichaAtendimento.SituacaoSuspeito.L, FichaAtendimento.SituacaoSuspeito.R});
        model.addAttribute("estadosConsulta", estadoConsultaService.findAll());
        model.addAttribute("situacaoSuspeito", situacaoSuspeito);
        return "atendimento/suspeitos/lista";
    }

    @GetMapping("/user/suspeitos/abre/{id}")
    public String listaFichas(@PathVariable("id") final Long idFicha, final ModelMap model, final Authentication authentication) {
        final FichaAtendimento ficha = fichaAtendimentoService.findById(idFicha).orElseThrow(() -> new IllegalArgumentException("Ficha não encontrada"));
        if (!ficha.isSuspeitoLeve()) {
            throw new IllegalArgumentException("Essa pessoa não é suspeita");
        }
        final FichaAtendimento.SituacaoSuspeito situacaoSuspeito = ficha.getSituacaoSuspeito();
        final Integer horasRetorno = situacaoSuspeito.getHorasRetorno();
        final LocalDateTime now = LocalDateTime.now();
        final Date limiteInteracao = DateUtils.castToDate(now.plusHours(-1 * horasRetorno));
        if ((Long) fichaAtendimentoService.countByCpfUltimaInteracao(ficha.getCpf(), limiteInteracao) > 0L) {
            throw new IllegalArgumentException("Esse paciente já interagiu dentro do período definido de " + horasRetorno + " horas");
        }
        final EstadoConsulta estado = estadoConsultaService.findByIndicadores(IndicadorEstado.AVALIACAO_RETORNO_SUSPEITO);
        ficha.setEstadoConsulta(estado);
        fichaAtendimentoService.save(ficha);
        return "redirect:/user/atendimento/recebe/" + ficha.getId();
    }
    
    @InitBinder
    protected void initDataBinder(final WebDataBinder binder){
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
