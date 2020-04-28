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
import br.ufsm.cpd.javatelemed.persistence.model.Cidade;
import br.ufsm.cpd.javatelemed.persistence.model.Estado;
import br.ufsm.cpd.javatelemed.persistence.model.EstadoConsulta;
import br.ufsm.cpd.javatelemed.persistence.model.FichaAtendimento;
import br.ufsm.cpd.javatelemed.persistence.model.Sexo;
import br.ufsm.cpd.javatelemed.persistence.service.CidadeService;
import br.ufsm.cpd.javatelemed.persistence.service.EstadoConsultaService;
import br.ufsm.cpd.javatelemed.persistence.service.EstadoService;
import br.ufsm.cpd.javatelemed.persistence.service.FichaAtendimentoService;
import br.ufsm.cpd.javatelemed.utils.RecaptchaUtils;
import br.ufsm.cpd.javatelemed.utils.TrampaUtils;
import io.micrometer.core.instrument.util.StringUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.DateUtils;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Controller
@RequestMapping("/atendimento/novo")
public class PacienteFichaAtendimentoFormController {

    @Autowired
    private EstadoConsultaService estadoConsultaService;
    @Autowired
    private FichaAtendimentoService fichaAtendimentoService;
    @Autowired
    private EstadoService estadoService;
    @Autowired
    private CidadeService cidadeService;
    private static final Integer IDADE_MINIMA = 15;
    private static final Integer IDADE_MAXIMA = 120;
    private static final Integer IDADE_MINIMA_RESPONSAVEL = 18;

    private static final String COMMAND_NAME = "command";

    @GetMapping
    public String showForm(final ModelMap modelMap) {
//        final Iterable<Estado> estados = estadoService.findAll();
//        modelMap.addAttribute("estados", estados);
        modelMap.addAttribute("sexos", Sexo.values());
        final EstadoConsulta estadoInicial = estadoConsultaService.findInicial();
        if (estadoInicial == null) {
            return "/atendimento/erroAtendimentoNaoDisponivel";
        }
//        modelMap.addAttribute("termoAceite", estadoInicial.getTermoEsclarecimento());
        modelMap.addAttribute("idadeMinimaPaciente", IDADE_MINIMA);
        return "atendimento/form";
    }

    @PostMapping
    public String submit(@Valid @ModelAttribute(COMMAND_NAME) final FichaAtendimento fichaAtendimento,
            final BindingResult errors,
            @RequestParam("g-recaptcha-response") final String tokenUserRecaptcha,
            final ModelMap modelMap,
            final HttpServletRequest request,
            final RedirectAttributes redirectAttributes) {

        if (!RecaptchaUtils.recaptcha(tokenUserRecaptcha)) {
            return showForm(modelMap);
        }

        if (errors.hasErrors()) {
            return showForm(modelMap);
        }
        if (!validateLocal(fichaAtendimento, errors)) {
            return showForm(modelMap);
        }

        if (fichaAtendimento.getCidade() != null) {
            final Cidade cidade = fichaAtendimento.getCidade();
            final Cidade cidadeSede = fichaAtendimento.getCidadeSedeAtendimento();
            if ((!cidadeSede.equals(cidade) && CollectionUtils.isEmpty(cidadeSede.getCidadesAtendidas()))
                    || (!cidadeSede.equals(cidade) && !cidadeSede.getCidadesAtendidas().contains(cidade))) {
                errors.rejectValue("cidade", "errors.invalid");
                return showForm(modelMap);
            }
        }
        // Salva com protocolo e senha
        final EstadoConsulta estadoInicial = estadoConsultaService.findInicial();
        fichaAtendimento.setEstadoConsulta(estadoInicial);
        fichaAtendimento.setIp(TrampaUtils.getIp(request));
        final FichaAtendimento ficha = fichaAtendimentoService.saveWithProtocoloSenha(fichaAtendimento);
//        modelMap.addAttribute("ficha", ficha);
        redirectAttributes.addAttribute("protocolo", ficha.getProtocolo());
        redirectAttributes.addAttribute("senha", ficha.getSenha());
        return "redirect:/atendimento/visualizar";
    }

    @ModelAttribute(COMMAND_NAME)
    protected FichaAtendimento getCommand(@RequestParam("sede") final Long idCidadeSede) {
        final Cidade cidade = cidadeService.findById(idCidadeSede).orElseThrow(() -> new IllegalArgumentException("Cidade não encontrada"));
        if (!cidade.getAtendimento()) {
            throw new IllegalArgumentException("Cidade não está realizando atendimentos");
        }
        final FichaAtendimento fichaAtendimento = new FichaAtendimento();
        fichaAtendimento.setCidadeSedeAtendimento(cidade);
        return fichaAtendimento;
    }

    @InitBinder(COMMAND_NAME)
    protected void bind(final WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
        binder.registerCustomEditor(Estado.class, new EntidadeEditor(estadoService));
        binder.registerCustomEditor(Cidade.class, new EntidadeEditor(cidadeService));
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    private boolean validateLocal(FichaAtendimento fichaAtendimento, BindingResult errors) {
        final Date now = DateUtils.createNow().getTime();
        if (fichaAtendimento.getDataNascimento().after(now)) {
            errors.rejectValue("dataNascimento", "errors.invalid");
//        } else if (fichaAtendimento.getIdade() < IDADE_MINIMA) {
//            errors.rejectValue("dataNascimento", "errors.idadeMinima", new String[]{String.valueOf(IDADE_MINIMA)}, null);
        } else if (fichaAtendimento.getIdade() > IDADE_MAXIMA) {
            errors.rejectValue("dataNascimento", "errors.invalid");
        }
        if (fichaAtendimento.getDataInicialSintomas() != null && fichaAtendimento.getDataInicialSintomas().after(now)) {
            errors.rejectValue("dataInicialSintomas", "errors.invalid");
        }
        if (!fichaAtendimento.getDeclaracaoVeracidade()) {
            errors.rejectValue("declaracaoVeracidade", "errors.fichaAtendimento.declaracaoVeracidade");
        }
        if (fichaAtendimento.getDataNascimento() != null && fichaAtendimento.getIdade() < IDADE_MINIMA) {
            if (StringUtils.isEmpty(fichaAtendimento.getNomeResponsavelLegal())) {
                errors.rejectValue("nomeResponsavelLegal", "errors.required");
            }
            if (StringUtils.isEmpty(fichaAtendimento.getCpfResponsavelLegal())) {
                errors.rejectValue("cpfResponsavelLegal", "errors.required");
            } else if (fichaAtendimento.getCpf().equalsIgnoreCase(fichaAtendimento.getCpfResponsavelLegal())) {
                errors.rejectValue("cpfResponsavelLegal", "errors.cpfResponsavelIgualPaciente");
            }
            if (fichaAtendimento.getDataNascimentoResponsavelLegal() == null) {
                errors.rejectValue("dataNascimentoResponsavelLegal", "errors.required");
            } else if (fichaAtendimento.getIdadeResponsavelLegal() < IDADE_MINIMA_RESPONSAVEL) {
                errors.rejectValue("dataNascimentoResponsavelLegal", "errors.idadeMinimaResponsavel", new String[]{String.valueOf(IDADE_MINIMA_RESPONSAVEL)}, null);
            }
        }
        if (StringUtils.isNotBlank(fichaAtendimento.getTelefone()) && (fichaAtendimento.getTelefone().length() < 13
                || (fichaAtendimento.getTelefone().charAt(4) == '9' && fichaAtendimento.getTelefone().length() != 14))) {
            errors.rejectValue("telefone", "errors.invalid");
        }
        /*
        final RestTemplate restTemplate = new RestTemplate();
        final Endereco endereco = restTemplate.getForObject("http://viacep.com.br/ws/" + fichaAtendimento.getCep() + "/json/", Endereco.class);
        if (endereco == null) {
            errors.rejectValue("cep", "errors.invalid");
        } else {
            if (StringUtils.compareIgnoreCase(endereco.getBairro(), fichaAtendimento.getBairro()) != 0) {
                errors.rejectValue("bairro", "errors.invalid");
            }
            if (StringUtils.compareIgnoreCase(endereco.getLogradouro(), fichaAtendimento.getEndereco()) != 0) {
                errors.rejectValue("endereco", "errors.invalid");
            }
            if (fichaAtendimento.getCidade().getCodigo().equals(endereco.getIbge())) {
                errors.rejectValue("cidade", "errors.invalid");
        }
            }*/
        return !errors.hasErrors();
    }
    /*
    public static final class Endereco {

        private String cep;
        private String logradouro;
        private String bairro;
        private String ibge;

        public Endereco() {
        }

        public String getCep() {
            return cep;
        }

        public void setCep(String cep) {
            this.cep = cep;
        }

        public String getLogradouro() {
            return logradouro;
        }

        public void setLogradouro(String logradouro) {
            this.logradouro = logradouro;
        }

        public String getBairro() {
            return bairro;
        }

        public void setBairro(String bairro) {
            this.bairro = bairro;
        }

        public String getIbge() {
            return ibge;
        }

        public void setIbge(String ibge) {
            this.ibge = ibge;
        }

    }*/
}
