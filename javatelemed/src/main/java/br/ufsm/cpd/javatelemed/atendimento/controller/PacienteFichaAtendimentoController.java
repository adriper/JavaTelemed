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

import br.ufsm.cpd.javatelemed.commons.collection.CollectionUtils;
import br.ufsm.cpd.javatelemed.commons.web.editor.EntidadeEditor;
import br.ufsm.cpd.javatelemed.persistence.model.Cidade;
import br.ufsm.cpd.javatelemed.persistence.model.Estado;
import br.ufsm.cpd.javatelemed.persistence.model.FichaAtendimento;
import br.ufsm.cpd.javatelemed.persistence.model.NotaAtendimento;
import br.ufsm.cpd.javatelemed.persistence.service.AtendimentoService;
import br.ufsm.cpd.javatelemed.persistence.service.CidadeService;
import br.ufsm.cpd.javatelemed.persistence.service.FichaAtendimentoService;
import br.ufsm.cpd.javatelemed.persistence.service.NotaAtendimentoService;
import br.ufsm.cpd.javatelemed.utils.DateUtils;
import java.time.LocalDateTime;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Controller
public class PacienteFichaAtendimentoController {

    @Autowired
    private FichaAtendimentoService fichaAtendimentoService;
    @Autowired
    private CidadeService cidadeService;
    @Autowired
    AtendimentoService atendimentoService;
    @Autowired
    NotaAtendimentoService NotaAtendimentoService;

    private static final Integer HORAS_LIMITE_ACESSO = 4;

    public FichaAtendimento getFicha(final String salaAtendimento) {
        final FichaAtendimento ficha = fichaAtendimentoService.findBySalaAtendimento(salaAtendimento);
        return getFicha(ficha);

    }

    public FichaAtendimento getFicha(final String protocolo, final String senha) {
        final FichaAtendimento ficha = fichaAtendimentoService.findByProtocolo(protocolo);
        if (ficha == null) {
            throw new IllegalArgumentException("Ficha não encontrada");
        }
        if (senha.trim().toLowerCase().compareTo(ficha.getSenha().toLowerCase()) != 0) {
            throw new IllegalArgumentException("Protocolo e senha não conferem");
        }
        return getFicha(ficha);

    }

    private FichaAtendimento getFicha(final FichaAtendimento ficha) {
        if (ficha.getEstadoConsulta().getAtendimentoFinalizado()) {
            final LocalDateTime now = LocalDateTime.now();
            if (ficha.getMomentoUltimaMensagem().before(DateUtils.castToDate(now.plusHours(-1 * HORAS_LIMITE_ACESSO)))) {
                throw new IllegalArgumentException("Atendimento finalizado há mais de " + HORAS_LIMITE_ACESSO + " horas.");
            }
        }
        return ficha;

    }

    @GetMapping("/atendimento/visualizar")
    protected String visualizar(@RequestParam("protocolo") final String protocolo, @RequestParam("senha") final String senha,
            final ModelMap modelMap) {
        try {
            final FichaAtendimento ficha = getFicha(protocolo, senha);
            modelMap.addAttribute("ficha", ficha);
            return "atendimento/pacienteView";
        } catch (IllegalArgumentException ex) {
            modelMap.addAttribute("msg", ex.getMessage());
            return "atendimento/erroPacienteView";
        }
    }

    @GetMapping({"/atendimento/cidade", "/atendimento"})
    protected String cidade(final ModelMap modelMap, final RedirectAttributes redirect) {
        try {
            final Collection<Estado> estados = cidadeService.findEstadosComCidadeSede();
            if (estados == null || estados.isEmpty()) {
                throw new RuntimeException("Nenhuma cidade possui atendimento");
            }
            final Collection<Cidade> cidades;
            if (estados.size() == 1) {
                cidades = cidadeService.findCidadesSede(estados.iterator().next().getId());
                if (CollectionUtils.isEmpty(cidades)) {
                    throw new RuntimeException("Nenhuma cidade está atendendo no estado indicado");
                }
                if (cidades.size() == 1) {
                    // apenas uma cidade, vamos indicar ela e redirecionar
                    redirect.addAttribute("sede", cidades.iterator().next().getId());
                    return "redirect:/atendimento/novo";
                }
            }
            modelMap.addAttribute("estados", estados);
            return "atendimento/selecionaCidade";
        } catch (RuntimeException ex) {
            modelMap.addAttribute("msg", ex.getMessage());
            return "atendimento/erroPacienteView";
        }
    }

    @PostMapping("/atendimento/nota")
    protected String nota(@RequestParam("protocolo") final String protocolo,
            @RequestParam("senha") final String senha,
            @RequestParam("mensagem") final String mensagem,
            final RedirectAttributes redirectAttributes) {
        try {
            final FichaAtendimento ficha = getFicha(protocolo, senha);
            if (ficha.getEstadoConsulta().getPermiteNotaPaciente()) {
                final NotaAtendimento nota = new NotaAtendimento();
                nota.setFichaAtendimento(ficha);
                nota.setDescricao(mensagem);
                atendimentoService.saveWithNotaPaciente(ficha, nota);
            }
        } catch (IllegalArgumentException ex) {
        }
        redirectAttributes.addAttribute("protocolo", protocolo);
        redirectAttributes.addAttribute("senha", senha);
        return "redirect:/atendimento/visualizar";
    }

    @GetMapping("/atendimento/nota/download")
    protected ResponseEntity<Resource> downloadArquivoNota(@RequestParam("protocolo") final String protocolo,
            @RequestParam("senha") final String senha,
            @RequestParam("nota") final NotaAtendimento nota,
            final RedirectAttributes redirectAttributes) {
        final FichaAtendimento ficha = getFicha(protocolo, senha);
        if (!nota.getFichaAtendimento().equals(ficha)) {
            throw new IllegalArgumentException("A nota passada não é da ficha em questão");
        }
        if (nota.getPossuiArquivo()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(nota.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nota.getFileName() + "\"")
                    .body(new ByteArrayResource(nota.getFileData()));
        }
        return null;
    }

    @InitBinder
    protected void binder(final WebDataBinder binder) {
        binder.registerCustomEditor(NotaAtendimento.class, new EntidadeEditor(NotaAtendimentoService));
    }

}
