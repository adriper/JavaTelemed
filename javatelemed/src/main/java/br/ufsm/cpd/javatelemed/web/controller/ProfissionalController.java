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
import br.ufsm.cpd.javatelemed.persistence.model.Especialidade;
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.model.TipoProfissional;
import br.ufsm.cpd.javatelemed.persistence.service.CidadeService;
import br.ufsm.cpd.javatelemed.persistence.service.EspecialidadeService;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalService;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalServiceBean;
import br.ufsm.cpd.javatelemed.persistence.service.TipoProfissionalService;
import br.ufsm.cpd.javatelemed.utils.TrampaUtils;
import io.micrometer.core.instrument.util.StringUtils;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
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
public class ProfissionalController {

    @Autowired
    private ProfissionalService profissionalService;
    @Autowired
    private ProfissionalServiceBean profissionalServiceBean;
    @Autowired
    private TipoProfissionalService tipoProfissionalService;
    @Autowired
    private EspecialidadeService especialidadeService;
    @Autowired
    private CidadeService cidadeService;

    @RequestMapping("/admin/profissional/lista/")
    public String lista(final ModelMap model,
            @PageableDefault final Pageable pageable,
            @RequestParam(name = "tipoProfissional", required = false) final TipoProfissional tipoProfissional,
            @RequestParam(name = "nome", required = false) final String nome,
            @RequestParam(name = "cpf", required = false) final String cpf,
            @RequestParam(name = "numeroRegistro", required = false) final String numeroRegistro,
            final Authentication authentication) {
        final Profissional usuario = (Profissional) authentication.getPrincipal();
        final Page<Profissional> page = profissionalServiceBean.findByTipoNomeCpfRegistro(usuario.getPossiveisPapeisCadastroProfissional(), tipoProfissional, nome, cpf, numeroRegistro, pageable);
        model.addAttribute("page", page);
        model.addAttribute("tiposProfissionais", tipoProfissionalService.findAll());
        model.addAttribute("tipoProfissional", tipoProfissional);
        model.addAttribute("nome", nome);
        model.addAttribute("cpf", cpf);
        model.addAttribute("numeroRegistro", numeroRegistro);
        model.addAttribute("tipoProfissional", tipoProfissional);
        model.addAttribute("url", buildUrlLista(tipoProfissional, nome, cpf, numeroRegistro));
        model.addAttribute("buscou", verificaSeBuscou(tipoProfissional, nome, cpf, numeroRegistro));
        return "/profissional/lista";
    }

    @RequestMapping("/admin/profissional/view/{id}")
    public String view(@PathVariable("id") final Long id, final ModelMap model) {
        final Optional<Profissional> profissional = profissionalService.findById(id);
        model.addAttribute("profissional", profissional.orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado")));
        model.addAttribute("podeEditar", true);
        return "/profissional/view";
    }

    @RequestMapping("/user/profissional/view/{id}")
    public String userview(@PathVariable("id") final Long id, final ModelMap model) {
        final Profissional profissional = profissionalService.findById(id).orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));
        model.addAttribute("profissional", profissional);
        return "/profissional/userView";
    }

    @RequestMapping("/user/profissional/view/")
    public String viewProprio(final Authentication authentication, final ModelMap model) {
        final Optional<Profissional> profissional = profissionalService.findById(((Profissional) authentication.getPrincipal()).getId());
        model.addAttribute("profissional", profissional.orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado")));
        model.addAttribute("podeEditar", false);
        return "/profissional/view";
    }

    @RequestMapping("/admin/profissional/ativar/{id}")
    public String ativar(@PathVariable("id") final Long id, final ModelMap model) {
        final Profissional profissional = profissionalService.findById(id).orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));
        profissional.setAtivo(Boolean.TRUE);
        profissionalService.save(profissional);
        return "redirect:/admin/profissional/lista/";
    }

    @RequestMapping("/admin/profissional/inativar/{id}")
    public String inativar(@PathVariable("id") final Long id, final ModelMap model) {
        final Profissional profissional = profissionalService.findById(id).orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));
        profissional.setAtivo(Boolean.FALSE);
        profissionalService.save(profissional);
        return "redirect:/admin/profissional/lista/";
    }

    @RequestMapping("/admin/profissional/deleta/{id}")
    public String deleta(@PathVariable("id") final Long id, final ModelMap model) {
        profissionalService.deleteById(id);
        return "redirect:/admin/profissional/lista/";
    }

    @RequestMapping("/admin/profissional/deletaEspecialidade/{id}")
    public String deletaEspecialidade(@PathVariable("id") final Long id, @RequestParam("especialidade") final Especialidade especialidade,
            final ModelMap model) {
        final Profissional profissional = profissionalService.findById(id).orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));
        profissional.getEspecialidades().remove(especialidade);
        profissionalService.save(profissional);
        return "redirect:/admin/profissional/view/" + profissional.getId();
    }

    @RequestMapping("/admin/profissional/deletaCidadeSede/{id}")
    public String deletaCidadeSede(@PathVariable("id") final Long id,
            @RequestParam("cidadeSede") final Cidade cidadeSede,
            final ModelMap model) {
        final Profissional profissional = profissionalService.findById(id).orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));
        profissional.getCidadesSede().remove(cidadeSede);
        profissionalService.save(profissional);
        return "redirect:/admin/profissional/view/" + profissional.getId();
    }

    @RequestMapping("/admin/profissional/emailCadastro/{id}")
    public String emailCadastro(@PathVariable("id") final Long idProfissional,
            final ModelMap model,
            final HttpServletRequest request) {
        final Profissional profissional = profissionalService.findById(idProfissional).orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));
        profissionalServiceBean.enviarEmailCadastroProfissional(profissional, TrampaUtils.getHost(request));
        return "redirect:/admin/profissional/view/" + idProfissional;
    }

    private String buildUrlLista(TipoProfissional tipoProfissional, String nome, String cpf, String numeroRegistro) {
        final StringBuilder builder = new StringBuilder();
        builder.append("/admin/profissional/lista/");
        builder.append("?tipoProfissional=").append(tipoProfissional != null ? tipoProfissional.getId().toString() : "");
        builder.append("&cpf=").append(StringUtils.isNotBlank(cpf) ? cpf : "");
        builder.append("&nome=").append(StringUtils.isNotBlank(nome) ? nome : "");
        builder.append("&numeroRegistro=").append(StringUtils.isNotBlank(numeroRegistro) ? numeroRegistro : "");
        return builder.toString();
    }

    @InitBinder
    protected void binder(final WebDataBinder binder) {
        binder.registerCustomEditor(TipoProfissional.class, new EntidadeEditor(tipoProfissionalService));
        binder.registerCustomEditor(Cidade.class, new EntidadeEditor(cidadeService));
        binder.registerCustomEditor(Especialidade.class, new EntidadeEditor(especialidadeService));
    }

    private boolean verificaSeBuscou(TipoProfissional tipoProfissional, String nome, String cpf, String numeroRegistro) {
        return (tipoProfissional != null || StringUtils.isNotBlank(nome) || StringUtils.isNotBlank(cpf) || StringUtils.isNotBlank(numeroRegistro));
    }
}
