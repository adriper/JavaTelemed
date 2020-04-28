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
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.model.ProfissionalPreceptor;
import br.ufsm.cpd.javatelemed.persistence.model.TipoProfissional;
import br.ufsm.cpd.javatelemed.persistence.service.CidadeService;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalPreceptorService;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalService;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalServiceBean;
import br.ufsm.cpd.javatelemed.persistence.service.TipoProfissionalService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
public abstract class PreceptorFormController {

    @Autowired
    protected TipoProfissionalService tipoProfissionalService;
    @Autowired
    protected ProfissionalPreceptorService profissionalPreceptorService;
    @Autowired
    protected ProfissionalService profissionalService;
    @Autowired
    protected CidadeService cidadeService;
    @Autowired
    protected ProfissionalServiceBean profissionalServiceBean;
    protected static final String COMMAND_NAME = "command";

    protected abstract String getFormView();

    protected abstract String getSuccessView();

    protected abstract String getUrlInativa();

    protected abstract String getUrlForm(final ProfissionalPreceptorCommand command);

    protected abstract Profissional getProfissional(final Long idProfissional, final Authentication authentication);

    protected Collection<Cidade> findCidadesSede(ProfissionalPreceptorCommand command) {
        final Profissional preceptor = command.getPreceptor();
        final Set<Cidade> cidadesSede = preceptor.getCidadesSede();
        if (CollectionUtils.isEmpty(cidadesSede)) {
            throw new IllegalArgumentException("Você não está vinculado a nenhuma cidade sede, portanto não pode ser um preceptor");
        }
        return cidadesSede;
    }

    @ModelAttribute(COMMAND_NAME)
    protected ProfissionalPreceptorCommand getCommand(@PathVariable(name = "id", required = false) final Long idProfissional, @RequestParam(name = "idCidadeSede", required = false) final Long idCidadeSede,
            @RequestParam(name = "idTipoProfissional", required = false) final Long idTipoProfissional, final Authentication authentication) {
        final Profissional profissional = getProfissional(idProfissional, authentication);

        if (!profissional.getAtivo()) {
            throw new IllegalArgumentException("Profissional não está ativo");
        }
        if (!profissional.podeSerPreceptor()) {
            throw new IllegalArgumentException("Este profissional não pode ser um preceptor");
        }
        final ProfissionalPreceptorCommand command = new ProfissionalPreceptorCommand();
        command.setCidadeSede(getCidadeSede(idCidadeSede));
        command.setTipoProfissional(getTipoProfissional(idTipoProfissional));
        command.setPreceptor(profissional);
//        command.setAlunos(profissional.getAlunos());
        return command;
    }

    private Cidade getCidadeSede(final Long idCidadeSede) {
        if (idCidadeSede == null) {
            return null;
        }
        final Cidade cidadeSede = cidadeService.findById(idCidadeSede).orElseThrow(() -> new IllegalArgumentException("Cidade não encontrada"));
        if (!cidadeSede.getAtendimento()) {
            throw new IllegalArgumentException("A cidade indicada não é uma sede de atendimento");
        }
        return cidadeSede;
    }

    private TipoProfissional getTipoProfissional(final Long idTipoProfissional) {
        if (idTipoProfissional == null) {
            return null;
        }
        return tipoProfissionalService.findById(idTipoProfissional).orElseThrow(() -> new IllegalArgumentException("Tipo profissional não encontrado"));
    }

    @GetMapping
    protected String showForm(@ModelAttribute(COMMAND_NAME) final ProfissionalPreceptorCommand command,
            final ModelMap modelMap) {
        final Cidade cidadeSede = command.getCidadeSede();
        final TipoProfissional tipoProfissional = command.getTipoProfissional();
        modelMap.addAttribute("tiposProfissionais", tipoProfissionalService.findAllSemConselho());
        modelMap.addAttribute("cidadesSede", findCidadesSede(command));
        modelMap.addAttribute("urlInativa", getUrlInativa());
        modelMap.addAttribute("urlForm", getUrlForm(command));

        if (cidadeSede != null && tipoProfissional != null) {
            // Busca profissionais indicados e adiciona 
            modelMap.addAttribute("profissionais", profissionalService.findByTipoProfissionalCidadeSede(tipoProfissional, cidadeSede, command.getPreceptor()));
        }
        return getFormView();
    }

    @PostMapping
    protected String post(@ModelAttribute(COMMAND_NAME) @Valid final ProfissionalPreceptorCommand command, final BindingResult errors,
            final ModelMap modelMap) {
        if (!errors.hasErrors()) {
            if (!command.getValidade().after(command.getInicio())) {
                errors.rejectValue("validade", "errors.invalid");
                errors.rejectValue("inicio", "errors.invalid");
            }
        }
        if (errors.hasErrors()) {
            return showForm(command, modelMap);
        } else {
            final List<ProfissionalPreceptor> profissionaisPreceptor = new ArrayList<ProfissionalPreceptor>();
            for (final Profissional aluno : command.getAlunos()) {
                final ProfissionalPreceptor profissionalPreceptor = new ProfissionalPreceptor();
                profissionalPreceptor.setAluno(aluno);
                profissionalPreceptor.setPreceptor(command.getPreceptor());
                profissionalPreceptor.setValidade(command.getValidade());
                profissionalPreceptor.setInicio(command.getInicio());
                profissionalPreceptor.setAtivo(true);
                profissionaisPreceptor.add(profissionalPreceptor);

                if (((Long) profissionalPreceptorService.countByEstudanteInicioFim(aluno, command.getInicio(), command.getValidade())) > 0L) {
                    errors.rejectValue("alunos", "errors.preceptor.aluno.exists", new Object[]{aluno.getNome()}, null);
                    break;
                }
            }
            if (!errors.hasErrors()) {
                profissionalPreceptorService.saveAll(profissionaisPreceptor);
            }
        }
        return showForm(command, modelMap);
    }

    @InitBinder
    protected void binder(final WebDataBinder binder) {
        binder.registerCustomEditor(Profissional.class, new EntidadeEditor(profissionalService));
        binder.registerCustomEditor(TipoProfissional.class, new EntidadeEditor(tipoProfissionalService));
        binder.registerCustomEditor(Cidade.class, new EntidadeEditor(cidadeService));
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy HH:mm"), true));
    }

    public static class ProfissionalPreceptorCommand {

        private Cidade cidadeSede;
        private TipoProfissional tipoProfissional;
        @NotEmpty
        private Collection<Profissional> alunos;
        @NotNull
        @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
        private Date validade;
        @NotNull
        @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
        private Date inicio;
        private Profissional preceptor;

        public Collection<Profissional> getAlunos() {
            return alunos;
        }

        public void setAlunos(Collection<Profissional> alunos) {
            this.alunos = alunos;
        }

        public Date getValidade() {
            return validade;
        }

        public void setValidade(Date validade) {
            this.validade = validade;
        }

        public ProfissionalPreceptorCommand() {
            alunos = new ArrayList<Profissional>();
        }

        public Profissional getPreceptor() {
            return preceptor;
        }

        public void setPreceptor(Profissional preceptor) {
            this.preceptor = preceptor;
        }

        public Cidade getCidadeSede() {
            return cidadeSede;
        }

        public void setCidadeSede(Cidade cidadeSede) {
            this.cidadeSede = cidadeSede;
        }

        public TipoProfissional getTipoProfissional() {
            return tipoProfissional;
        }

        public void setTipoProfissional(TipoProfissional tipoProfissional) {
            this.tipoProfissional = tipoProfissional;
        }

        public Date getInicio() {
            return inicio;
        }

        public void setInicio(Date inicio) {
            this.inicio = inicio;
        }

    }
}
