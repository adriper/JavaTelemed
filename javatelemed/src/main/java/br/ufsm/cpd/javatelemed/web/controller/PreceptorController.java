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
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.model.ProfissionalPreceptor;
import br.ufsm.cpd.javatelemed.persistence.model.TipoProfissional;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalPreceptorService;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalService;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalServiceBean;
import br.ufsm.cpd.javatelemed.persistence.service.TipoProfissionalService;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Controller
public class PreceptorController {

    @Autowired
    private ProfissionalServiceBean profissionalServiceBean;
    @Autowired
    private ProfissionalService profissionalService;
    @Autowired
    private TipoProfissionalService tipoProfissionalService;
    @Autowired
    private ProfissionalPreceptorService profissionalPreceptorService;

    @GetMapping("/admin/profissional/preceptor/busca/")
    protected String buscaPreceptorGet(final ModelMap modelMap) {
        modelMap.addAttribute("tiposProfissional", tipoProfissionalService.findAll());
        return "profissional/preceptor/buscaProfissional";
    }

    @PostMapping("/admin/profissional/preceptor/busca/")
    protected String buscaPerceptorSubmit(
            @RequestParam(name = "tipoProfissional", required = false) final TipoProfissional tipoProfissional,
            @RequestParam(name = "nome", required = false) final String nome,
            @RequestParam(name = "cpf", required = false) final String cpf,
            @RequestParam(name = "numeroRegistro", required = false) final String numeroRegistro,
            final ModelMap modelMap) {

        final Collection<Profissional> profissionais = profissionalServiceBean.findByTipoNomeCpfRegistro(tipoProfissional, nome, cpf, numeroRegistro);
        modelMap.addAttribute("profissionais", profissionais);
        return "profissional/preceptor/selecionaProfissional";
    }

    @PostMapping("/admin/profissional/preceptor/inativa/")
    protected String inativaAlunosAdmin(@RequestParam("alunosInativar") final Collection<ProfissionalPreceptor> alunosInativar,
            @RequestParam("preceptor") final Profissional preceptor,
            final RedirectAttributes redirectAttributes) {
        inativa(alunosInativar, preceptor);
        return "redirect:/admin/profissional/preceptor/" + preceptor.getId();
    }

    @GetMapping("/admin/profissional/preceptor/inativa/{id}")
    protected String inativaAlunoAdmin(@PathVariable("id") final ProfissionalPreceptor preceptorProfissional,
            final RedirectAttributes redirectAttributes) {
        preceptorProfissional.setAtivo(false);
        profissionalPreceptorService.save(preceptorProfissional);
        return "redirect:/admin/profissional/view/" + preceptorProfissional.getPreceptor().getId();
    }

    @PostMapping("/user/preceptor/inativa/")
    protected String inativaAlunosUser(@RequestParam("alunosInativar") final Collection<ProfissionalPreceptor> alunosInativar,
            Authentication authentication,
            final RedirectAttributes redirectAttributes) {
        final Profissional preceptor = (Profissional) authentication.getPrincipal();
        inativa(alunosInativar, preceptor);
        return "redirect:/user/preceptor/";
    }

    private void inativa(final Collection<ProfissionalPreceptor> alunosInativar, final Profissional preceptor) throws IllegalArgumentException {
        for (final ProfissionalPreceptor aluno : alunosInativar) {
            if (!aluno.getPreceptor().equals(preceptor)) {
                throw new IllegalArgumentException("O identificador do aluno passado não pertence ao preceptor");
            }
            aluno.setAtivo(false);
        }
        profissionalPreceptorService.saveAll(alunosInativar);
    }

    @InitBinder
    protected void binder(final WebDataBinder binder) {
        binder.registerCustomEditor(TipoProfissional.class, new EntidadeEditor(tipoProfissionalService));
        binder.registerCustomEditor(Profissional.class, new EntidadeEditor(profissionalService));
        binder.registerCustomEditor(ProfissionalPreceptor.class, new EntidadeEditor(profissionalPreceptorService));
    }
}
