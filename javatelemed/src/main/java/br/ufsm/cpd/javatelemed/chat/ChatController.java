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
package br.ufsm.cpd.javatelemed.chat;

import br.ufsm.cpd.javatelemed.atendimento.controller.PacienteFichaAtendimentoController;
import br.ufsm.cpd.javatelemed.persistence.model.EstadoConsulta;
import br.ufsm.cpd.javatelemed.persistence.model.FichaAtendimento;
import br.ufsm.cpd.javatelemed.persistence.model.NotaAtendimento;
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.service.AtendimentoService;
import br.ufsm.cpd.javatelemed.persistence.service.FichaAtendimentoService;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalService;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Controller
public class ChatController {

    @Autowired
    private FichaAtendimentoService fichaAtendimentoService;
    @Autowired
    private ProfissionalService profissionalService;
    @Autowired
    private AtendimentoService atendimentoService;
    @Autowired
    private PacienteFichaAtendimentoController pacienteFichaAtendimentoController;
    @Autowired
    @Qualifier("dateTimeFormat")
    private DateFormat simpleDateFormat;

    @MessageMapping("/atendimentoprofissional/{sala}")
    @SendTo("/topic/chat/{sala}")
    public OutputMessage profissional(@DestinationVariable("sala") final String sala,
            InputMessage message,
            Authentication authentication) throws Exception {
        final Profissional profissional = getProfissional(authentication);
        FichaAtendimento fichaAtendimento = fichaAtendimentoService.atendeFicha(sala, profissional);
        if (!fichaAtendimento.getEstadoConsulta().getPermiteNotaProfissional()) {
            throw new IllegalArgumentException("O estado atual não permite o envio de mensagens pelo profissional");
        }
        final NotaAtendimento notaAtendimento = new NotaAtendimento();
        notaAtendimento.setDescricao(message.getMessage());
        notaAtendimento.setProfissional(fichaAtendimento.getResponsavel());
        notaAtendimento.setEstudante(fichaAtendimento.getEstudante() != null && fichaAtendimento.getEstudante().equals(profissional) ? profissional : null);
        atendimentoService.saveWithNotaProfissional(fichaAtendimento, notaAtendimento);
        fichaAtendimento = fichaAtendimentoService.findBySalaAtendimento(sala);
        return new OutputMessage(notaAtendimento, simpleDateFormat.format(notaAtendimento.getMomentoCriacao()), fichaAtendimento.getEstadoConsulta().getAtendimentoFinalizado(), processaProximosEstados(fichaAtendimento));
    }

    @MessageMapping("/atendimentopaciente/{sala}")
    @SendTo("/topic/chat/{sala}")
    public OutputMessage paciente(@DestinationVariable("sala") final String sala,
            InputMessage message) throws Exception {
        FichaAtendimento ficha = pacienteFichaAtendimentoController.getFicha(sala);
        final NotaAtendimento notaAtendimento = new NotaAtendimento();
        notaAtendimento.setDescricao(message.getMessage());
        atendimentoService.saveWithNotaPaciente(ficha, notaAtendimento);
        ficha = fichaAtendimentoService.findBySalaAtendimento(sala);
        return new OutputMessage(notaAtendimento, simpleDateFormat.format(notaAtendimento.getMomentoCriacao()), ficha.getEstadoConsulta().getAtendimentoFinalizado(), processaProximosEstados(ficha));
    }

    private Collection<EstadoConsultaDto> processaProximosEstados(final FichaAtendimento fichaAtendimento) {
        final ArrayList<EstadoConsultaDto> proximosEstadosPossiveis = new ArrayList<>();
        proximosEstadosPossiveis.add(new EstadoConsultaDto(fichaAtendimento.getEstadoConsulta()));
        fichaAtendimento.getEstadoConsulta().getProximosEstadosPossiveis().forEach((e) -> proximosEstadosPossiveis.add(new  EstadoConsultaDto(e)));
        return proximosEstadosPossiveis;

    }
  
    private Profissional getProfissional(Authentication authentication) {
        return profissionalService.findById(((Profissional) authentication.getPrincipal()).getId()).orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));
    }
    
    public static class EstadoConsultaDto implements Serializable{
        private Long id;
        private String descricao;

        public EstadoConsultaDto() {
        }
        

        public EstadoConsultaDto(final EstadoConsulta estadoConsulta) {
            this.id = estadoConsulta.getId();
            this.descricao = estadoConsulta.getDescricao();
        }
        
        public EstadoConsultaDto(Long id, String descricao) {
            this.id = id;
            this.descricao = descricao;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
        
    }
}
