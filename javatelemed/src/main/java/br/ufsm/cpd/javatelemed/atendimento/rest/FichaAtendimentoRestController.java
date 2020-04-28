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
package br.ufsm.cpd.javatelemed.atendimento.rest;

import br.ufsm.cpd.javatelemed.atendimento.controller.PacienteFichaAtendimentoController;
import br.ufsm.cpd.javatelemed.exception.FichaAtendimentoException;
import br.ufsm.cpd.javatelemed.persistence.model.EstadoConsulta;
import br.ufsm.cpd.javatelemed.persistence.model.FichaAtendimento;
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.service.AtendimentoService;
import br.ufsm.cpd.javatelemed.persistence.service.FichaAtendimentoService;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalService;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@RestController
public class FichaAtendimentoRestController {

    @Autowired
    private FichaAtendimentoService fichaAtendimentoService;
    @Autowired
    private ProfissionalService profissionalService;
    @Autowired
    private AtendimentoService atendimentoService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private PacienteFichaAtendimentoController pacienteFichaAtendimentoController;

//    @GetMapping("/user/atendimento/notas/{id}")
    protected FichaAtendimentoNotasProximosEstados getNotas(@PathVariable("id") final Long id, final Authentication authentication) {
        final FichaAtendimento ficha = fichaAtendimentoService.findById(id).orElseThrow(() -> new IllegalArgumentException("Ficha não encontrada"));
        final Profissional profissional = profissionalService.findById(((Profissional) authentication.getPrincipal()).getId()).orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));
        final boolean podeVisualizar = fichaAtendimentoService.podeVisualizar(ficha, profissional);
        if (!podeVisualizar) {
            throw new IllegalArgumentException("Você não possuir permissão para acessar essa ficha");
        }
        final ArrayList<EstadoConsulta> proximosEstadosPossiveis = new ArrayList<>();
        proximosEstadosPossiveis.add(ficha.getEstadoConsulta());
        proximosEstadosPossiveis.addAll(ficha.getEstadoConsulta().getProximosEstadosPossiveis());
        return new FichaAtendimentoNotasProximosEstados(ficha.getNotas(profissional), proximosEstadosPossiveis);
    }

    @GetMapping("/user/atendimento/videoconferencia/{id}")
    protected Url videoconferencia(@PathVariable("id") final Long id, final Authentication authentication) throws FichaAtendimentoException {
        final Profissional profissional = profissionalService.findById(((Profissional) authentication.getPrincipal()).getId()).orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));
        if (!profissional.getTipoProfissional().getVideoconferencia()) {
            throw new IllegalArgumentException("Você não pode iniciar uma videoconferência");
        }
        final FichaAtendimento fichaAtendimento = fichaAtendimentoService.atendeFicha(id, profissional);
//        final LocalDateTime dataCriacao = LocalDateTime.now();
//        final String conteudo = id + dataCriacao.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//        final String hash = DigestUtils.sha256Hex(conteudo).substring(0, 16);
        final String message = messageSource.getMessage("br.ufsm.cpd.javatelemed.videoconferencia.url", null, null);
        return new Url(message + fichaAtendimento.getSalaAtendimento());
    }

//    @GetMapping("atendimento/notas")
    protected FichaAtendimentoNotasConcluido getNotasPaciente(@RequestParam("protocolo") final String protocolo,
            @RequestParam("senha") final String senha) {
        final FichaAtendimento ficha = pacienteFichaAtendimentoController.getFicha(protocolo, senha);
        atendimentoService.setMomentoAcessoPaciente(ficha);
        return new FichaAtendimentoNotasConcluido(ficha.getNotas(), ficha.getEstadoConsulta().getAtendimentoFinalizado());
    }

    @GetMapping("atendimento/cep/{cep}")
    protected Endereco getNotasPaciente(@PathVariable("cep") final String cep) {
        final RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.getForObject("http://viacep.com.br/ws/" + cep + "/json/", Endereco.class);
        } catch (Exception ex) {
            return new Endereco();
        }

    }

    public static final class Endereco {

        private boolean success = false;
        private String cep;
        private String logradouro;
        private String complemento;
        private String bairro;
        private String localidade;
        private String uf;
        private String unidade;
        private String ibge;

        public Endereco() {
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
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

        public String getComplemento() {
            return complemento;
        }

        public void setComplemento(String complemento) {
            this.complemento = complemento;
        }

        public String getBairro() {
            return bairro;
        }

        public void setBairro(String bairro) {
            this.bairro = bairro;
        }

        public String getLocalidade() {
            return localidade;
        }

        public void setLocalidade(String localidade) {
            this.localidade = localidade;
        }

        public String getUf() {
            return uf;
        }

        public void setUf(String uf) {
            this.uf = uf;
        }

        public String getUnidade() {
            return unidade;
        }

        public void setUnidade(String unidade) {
            this.unidade = unidade;
        }

        public String getIbge() {
            return ibge;
        }

        public void setIbge(String ibge) {
            this.ibge = ibge;
        }

        public boolean getPodeEditarLogradouro() {
            return ibge != null && (logradouro == null || StringUtils.isBlank(logradouro));
        }

    }

}
