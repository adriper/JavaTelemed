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

import br.ufsm.cpd.javatelemed.chat.ChatController;
import br.ufsm.cpd.javatelemed.chat.InputMessage;
import br.ufsm.cpd.javatelemed.chat.OutputMessage;
import br.ufsm.cpd.javatelemed.commons.web.editor.EntidadeEditor;
import br.ufsm.cpd.javatelemed.exception.FichaAtendimentoException;
import br.ufsm.cpd.javatelemed.persistence.model.EstadoConsulta;
import br.ufsm.cpd.javatelemed.persistence.model.FichaAtendimento;
import br.ufsm.cpd.javatelemed.persistence.model.NotaAtendimento;
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.service.AtendimentoService;
import br.ufsm.cpd.javatelemed.persistence.service.EstadoConsultaService;
import br.ufsm.cpd.javatelemed.persistence.service.FichaAtendimentoService;
import br.ufsm.cpd.javatelemed.persistence.service.NotaAtendimentoService;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalService;
import br.ufsm.cpd.javatelemed.utils.TrampaUtils;
import io.micrometer.core.instrument.util.StringUtils;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Controller
public class ProfissionalFichaAtencimentoController {

    @Autowired
    private FichaAtendimentoService fichaAtendimentoService;
    @Autowired
    private EstadoConsultaService estadoConsultaService;
    @Autowired
    private ProfissionalService profissionalService;
    @Autowired
    private NotaAtendimentoService notaAtendimentoService;
    @Autowired
    private AtendimentoService atendimentoService;
    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    @Qualifier("dateFormat")
    private DateFormat simpleDateFormat;
    @Autowired
    @Qualifier("dateTimeFormat")
    private DateFormat simpleDateTimeFormat;

    @PostMapping("/user/atendimento/arquivo/{id}")
    public String enviaArquivo(@PathVariable("id") final Long idAtendimento,
            Authentication authentication,
            final HttpServletRequest request,
            @RequestParam("arquivo") MultipartFile file) throws FichaAtendimentoException, IOException, Exception {
        final Profissional profissional = getProfissional(authentication);
        final FichaAtendimento fichaAtendimento = fichaAtendimentoService.atendeFicha(idAtendimento, profissional);
        if (!fichaAtendimento.getEstadoConsulta().getPermiteNotaProfissional()) {
            throw new IllegalArgumentException("O estado atual não permite o envio de mensagens pelo profissional");
        }
        final NotaAtendimento notaAtendimento = new NotaAtendimento();
        notaAtendimento.setDescricao("Envio de arquivo");
        notaAtendimento.setProfissional(fichaAtendimento.getResponsavel());
        notaAtendimento.setEstudante(fichaAtendimento.getEstudante() != null && fichaAtendimento.getEstudante().equals(profissional) ? profissional : null);
        if (file != null && !file.isEmpty()) {
            String fileName = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());
            notaAtendimento.setFileData(file.getBytes());
            notaAtendimento.setFileType(file.getContentType());
            notaAtendimento.setFileName(fileName);
        }
        NotaAtendimento notaSaved = atendimentoService.saveWithNotaProfissional(fichaAtendimento, notaAtendimento);
        this.template.convertAndSend("/topic/chat/" + fichaAtendimento.getSalaAtendimento(), new OutputMessage(notaSaved, simpleDateTimeFormat.format(notaAtendimento.getMomentoCriacao()), fichaAtendimento.getEstadoConsulta().getAtendimentoFinalizado()));
        return "redirect:/user/atendimento/recebe/" + idAtendimento;
    }

    @GetMapping("/user/atendimento/visualiza/{id}")
    public String visualizaFicha(@PathVariable("id") final Long idFicha,
            final Authentication authentication,
            final ModelMap modelMap) {
        final FichaAtendimento ficha = fichaAtendimentoService.findById(idFicha).orElseThrow(() -> new IllegalArgumentException("Ficha não encontrada"));
        final Profissional profissional = getProfissional(authentication);
        final boolean podeVisualizar = fichaAtendimentoService.podeVisualizar(ficha, profissional.getResponsavel());
        if (!podeVisualizar) {
            modelMap.addAttribute("msg", "Você não tem permissão para visualizar essa ficha");
            return "/atendimento/profissional/fichaIndisponivel";
        }
        modelMap.addAttribute("ficha", ficha);
        modelMap.addAttribute("podeEditar", false);
        return "atendimento/profissional/realizaAtendimento";
    }

    @PostMapping("/user/atendimento/suspeito/{id}")
    public String defineSuspeito(@PathVariable("id") final Long idAtendimento,
            @RequestParam("suspeito") final FichaAtendimento.SituacaoSuspeito suspeito,
            final Authentication authentication, final RedirectAttributes redirectAttributes) throws FichaAtendimentoException {
        final Profissional profissional = getProfissional(authentication);
        final FichaAtendimento fichaAtendimento = fichaAtendimentoService.atendeFicha(idAtendimento, profissional);
        if (fichaAtendimentoService.findSuspeitoByCpf(fichaAtendimento.getCpf(), fichaAtendimento.getId()) != null) {
            throw new IllegalArgumentException("Esta pessoa já foi indicada como suspeito em outro atendimento");
        } else {
            fichaAtendimento.setSituacaoSuspeito(suspeito);
            fichaAtendimentoService.save(fichaAtendimento);
        }
        return "redirect:/user/atendimento/recebe/" + idAtendimento;

    }

    @PostMapping("/user/atendimento/receita/{id}")
    public String receita(@PathVariable("id") final Long idAtendimento, @RequestParam("linkReceita") final String linkReceita,
            Authentication authentication) throws FichaAtendimentoException {
        final Profissional profissional = getProfissional(authentication);
        if (!profissional.getTipoProfissional().getPrescricao()) {
            throw new IllegalArgumentException("Você não pode prescrever uma receita");
        }
        final Profissional responsavel = profissional.getPreceptorResponsavel() != null ? profissional.getPreceptorResponsavel() : profissional;
        final FichaAtendimento fichaAtendimento = fichaAtendimentoService.atendeFicha(idAtendimento, profissional);
        final NotaAtendimento nota = new NotaAtendimento();
        nota.setFichaAtendimento(fichaAtendimento);
        nota.setDescricao("Por favor, acesse sua receita em: " + linkReceita);
        nota.setProfissional(responsavel);
        fichaAtendimento.setLinkReceita(linkReceita);
        atendimentoService.saveWithNotaProfissional(fichaAtendimento, nota);
        return "redirect:/user/atendimento/recebe/" + idAtendimento;

    }

    @PostMapping("/user/atendimento/estado/{id}")
    public String estado(@PathVariable("id") final Long idAtendimento,
            @RequestParam("estado") EstadoConsulta estado,
            Authentication authentication,
            final HttpServletRequest request) throws FichaAtendimentoException {
        if (estado == null) {
            throw new IllegalArgumentException("Estado não encontrado");
        }
        final Profissional profissional = getProfissional(authentication);
        final FichaAtendimento fichaAtendimento = fichaAtendimentoService.atendeFicha(idAtendimento, profissional);
        if (!estado.equals(fichaAtendimento.getEstadoConsulta()) && !fichaAtendimento.getEstadoConsulta().getProximosEstadosPossiveis().contains(estado)) {
            throw new IllegalArgumentException("O estado indicado não pode ser um dos próximos. Atualize a ficha antes de trocar seu estado");
        }
        // Vamos trocar o estado e criar uma nota para avisar o paciente
        final NotaAtendimento notaAtendimento = atendimentoService.trocaEstado(profissional, fichaAtendimento, estado, TrampaUtils.getHost(request));
        // Comunica paciente
        final OutputMessage outputMessage = new OutputMessage(notaAtendimento, simpleDateTimeFormat.format(notaAtendimento.getMomentoCriacao()), estado.getAtendimentoFinalizado(), processaProximosEstados(estado));
        this.template.convertAndSend("/topic/chat/" + fichaAtendimento.getSalaAtendimento(),
                outputMessage);
        if (estado.getAtendimentoFinalizado() || !profissional.getTipoProfissional().getEstadosConsulta().contains(estado)) {
            return "redirect:/user/atendimento/lista/";
        } else {
            return "redirect:/user/atendimento/recebe/" + idAtendimento;
        }

    }

    @PostMapping("/user/atendimento/nota/{id}")
    public String nota(@PathVariable("id") final Long idAtendimento,
            @RequestParam("nota") final String nota,
            Authentication authentication,
            @RequestParam("arquivoAnexo") MultipartFile file) throws FichaAtendimentoException, IOException {
        if (StringUtils.isBlank(nota)) {
            throw new IllegalArgumentException("A mensagem não pode estar em branco");
        }
        final Profissional profissional = getProfissional(authentication);
        final FichaAtendimento fichaAtendimento = fichaAtendimentoService.atendeFicha(idAtendimento, profissional);
        if (!fichaAtendimento.getEstadoConsulta().getPermiteNotaProfissional()) {
            throw new IllegalArgumentException("O estado atual não permite o envio de mensagens pelo profissional");
        }
        final NotaAtendimento notaAtendimento = new NotaAtendimento();
        notaAtendimento.setDescricao(nota);
        notaAtendimento.setProfissional(fichaAtendimento.getResponsavel());
        notaAtendimento.setEstudante(fichaAtendimento.getEstudante() != null && fichaAtendimento.getEstudante().equals(profissional) ? profissional : null);

        if (file != null && !file.isEmpty()) {
            String fileName = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());
            notaAtendimento.setFileData(file.getBytes());
            notaAtendimento.setFileType(file.getContentType());
            notaAtendimento.setFileName(fileName);
        }

        atendimentoService.saveWithNotaProfissional(fichaAtendimento, notaAtendimento);
        return "redirect:/user/atendimento/recebe/" + idAtendimento;

    }

    @GetMapping("/user/atendimento/nota/download/{id}")
    public ResponseEntity<Resource> downloadArquivo(@PathVariable("id") final Long idAtendimento,
            @RequestParam("nota") final NotaAtendimento nota,
            final ModelMap modelMap,
            Authentication authentication) throws FichaAtendimentoException {
        final FichaAtendimento ficha = fichaAtendimentoService.findById(idAtendimento).orElseThrow(() -> new IllegalArgumentException("Ficha não encontrada"));
        final Profissional profissional = getProfissional(authentication);
        final boolean podeVisualizar = fichaAtendimentoService.podeVisualizar(ficha, profissional.getResponsavel());
        if (!podeVisualizar) {
            throw new IllegalArgumentException("Você não tem permissão para acessar essa ficha");
        }
        if (nota.getFichaAtendimento().getId() != ficha.getId()) {
            throw new IllegalArgumentException("Esta nota não faz parte da ficha indicada");
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(nota.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nota.getFileName() + "\"")
                .body(new ByteArrayResource(nota.getFileData()));
    }

    @PostMapping("/user/atendimento/consideracoes/{id}")
    public String consideracoes(@PathVariable("id") final Long idAtendimento,
            @RequestParam("consideracoes") final String consideracoes,
            Authentication authentication) throws FichaAtendimentoException {
        if (StringUtils.isBlank(consideracoes)) {
            throw new IllegalArgumentException("As considerações não podem estar em branco");
        }
        final Profissional profissional = getProfissional(authentication);
        final FichaAtendimento fichaAtendimento = fichaAtendimentoService.atendeFicha(idAtendimento, profissional);
        if (!fichaAtendimento.getEstadoConsulta().getPermiteConsideracoesProfissional()) {
            throw new IllegalArgumentException("O estado atual não permite a alteração das considerações do profissional");
        }
        fichaAtendimento.processaConsideracoesProfissional(consideracoes, profissional);
        fichaAtendimentoService.save(fichaAtendimento);
        return "redirect:/user/atendimento/recebe/" + idAtendimento;

    }

    @GetMapping("/user/atendimento/lista/")
    public String listaFichas(final ModelMap model, final Authentication authentication, @PageableDefault final Pageable pageable,
            @RequestParam(name = "order", required = false, defaultValue = "momentoCriacao") final String order,
            @RequestParam(name = "protocolo", required = false) final String protocolo,
            @RequestParam(name = "paciente", required = false) final String paciente,
            @RequestParam(name = "cpf", required = false) final String cpf,
            @RequestParam(name = "dataInicio", required = false) final Date dataInicio,
            @RequestParam(name = "dataFim", required = false) final Date dataFim,
            @RequestParam(name = "estadoConsulta", required = false) final EstadoConsulta estado) throws Exception {
        model.addAttribute("estadosConsulta", estadoConsultaService.findAll());
//        model.addAttribute("page", fichaAtendimentoService.findAll(pageable));
        final Long id = ((Profissional) authentication.getPrincipal()).getId();
        final Profissional profissional = profissionalService.findById(id).orElseThrow(() -> new IllegalArgumentException("Erro!"));
        final Page<FichaAtendimento> page = atendimentoService.findAllValues(pageable, profissional, protocolo, paciente, cpf, estado, dataInicio, dataFim, order);
        boolean possuiNovaFicha = false;
        if (page.hasContent()) {
            for (final FichaAtendimento ficha : page.getContent()) {
                if (ficha.getEstadoConsulta().getPermiteAlterarProfissional()) {
                    possuiNovaFicha = true;
                }
            }
        }
        model.addAttribute("page", page);
        model.addAttribute("order", order);
        model.addAttribute("protocolo", protocolo);
        model.addAttribute("paciente", paciente);
        model.addAttribute("cpf", cpf);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("estadoConsulta", estado);
        model.addAttribute("possuiNovaFicha", possuiNovaFicha);
        model.addAttribute("buscou", StringUtils.isNotBlank(protocolo) || StringUtils.isNotBlank(paciente) || StringUtils.isNotBlank(cpf) || dataInicio != null || dataFim != null || estado != null);
        return "atendimento/profissional/listaFichas";
    }

    @InitBinder
    protected void binder(final WebDataBinder binder) {
        binder.registerCustomEditor(EstadoConsulta.class, new EntidadeEditor(estadoConsultaService));
        binder.registerCustomEditor(NotaAtendimento.class, new EntidadeEditor(notaAtendimentoService));
        binder.registerCustomEditor(Date.class, new CustomDateEditor(simpleDateFormat, true));
    }

    private Profissional getProfissional(Authentication authentication) {
        return profissionalService.findById(((Profissional) authentication.getPrincipal()).getId()).orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));
    }

    private Collection<ChatController.EstadoConsultaDto> processaProximosEstados(EstadoConsulta estado) {
        final ArrayList<ChatController.EstadoConsultaDto> proximosEstadosPossiveis = new ArrayList<>();
        proximosEstadosPossiveis.add(new ChatController.EstadoConsultaDto(estado));
        estado.getProximosEstadosPossiveis().forEach((e) -> proximosEstadosPossiveis.add(new ChatController.EstadoConsultaDto(e)));
        return proximosEstadosPossiveis;
    }

}
