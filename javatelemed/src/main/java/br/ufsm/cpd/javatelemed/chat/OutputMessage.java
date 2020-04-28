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

import br.ufsm.cpd.javatelemed.chat.ChatController.EstadoConsultaDto;
import br.ufsm.cpd.javatelemed.persistence.model.EstadoConsulta;
import br.ufsm.cpd.javatelemed.persistence.model.NotaAtendimento;
import java.util.Collection;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
public class OutputMessage {

    private String conteudo;
    private String autor;
    private String momentoEnvio;
    private Boolean paciente;
    private Collection<EstadoConsultaDto> proximosEstados;
    private boolean concluido;
    private boolean permiteNotaPaciente;
    private boolean permiteNotaProfissional;
    private boolean arquivo;
    private Long idNota;

    public OutputMessage() {
    }

    public OutputMessage(String conteudo, String autor, String momentoEnvio) {
        this.conteudo = conteudo;
        this.autor = autor;
        this.momentoEnvio = momentoEnvio;
    }

    public OutputMessage(String conteudo, String autor, String momentoEnvio, Boolean paciente, boolean concluido, boolean arquivo, Long idNota, boolean permiteNotaPaciente, boolean permiteNotaProfissional) {
        this.conteudo = conteudo;
        this.autor = autor;
        this.momentoEnvio = momentoEnvio;
        this.paciente = paciente;
        this.concluido = concluido;
        this.arquivo = arquivo;
        this.idNota = idNota;
        this.permiteNotaPaciente = permiteNotaPaciente;
        this.permiteNotaProfissional = permiteNotaProfissional;
    }
    

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getMomentoEnvio() {
        return momentoEnvio;
    }

    public void setMomentoEnvio(String momentoEnvio) {
        this.momentoEnvio = momentoEnvio;
    }

    public Boolean getPaciente() {
        return paciente;
    }

    public void setPaciente(Boolean paciente) {
        this.paciente = paciente;
    }

    public OutputMessage(final NotaAtendimento nota, final String momentoEnvio, final Collection<EstadoConsultaDto> proximosEstados) {
        this(nota, momentoEnvio);
        this.proximosEstados = proximosEstados;
    }
    public OutputMessage(final NotaAtendimento nota, final String momentoEnvio) {
        this.autor = nota.getNomeAutor();
        this.momentoEnvio = momentoEnvio;
        this.conteudo = nota.getDescricao();
        this.paciente = nota.getIsPaciente();
        permiteNotaPaciente = nota.getEstadoConsulta().getPermiteNotaPaciente();
        permiteNotaProfissional = nota.getEstadoConsulta().getPermiteNotaProfissional();
        this.arquivo = nota.getPossuiArquivo();
        this.idNota = nota.getId();
    }
        
    public OutputMessage(final NotaAtendimento nota, final String momentoEnvio, final Boolean concluido, Collection<EstadoConsultaDto> proximosEstados) {
        this(nota, momentoEnvio);
        this.concluido = concluido;
        this.proximosEstados = proximosEstados;
        
    }
    public OutputMessage(final NotaAtendimento nota, final String momentoEnvio, final Boolean concluido) {
        this(nota, momentoEnvio);
        this.concluido = concluido;
    }

    public Collection<EstadoConsultaDto> getProximosEstados() {
        return proximosEstados;
    }

    public void setProximosEstados(Collection<EstadoConsultaDto> proximosEstados) {
        this.proximosEstados = proximosEstados;
    }

    public boolean isConcluido() {
        return concluido;
    }

    public void setConcluido(boolean concluido) {
        this.concluido = concluido;
    }

    public boolean isPermiteNotaPaciente() {
        return permiteNotaPaciente;
    }

    public void setPermiteNotaPaciente(boolean permiteNotaPaciente) {
        this.permiteNotaPaciente = permiteNotaPaciente;
    }            

    public boolean isArquivo() {
        return arquivo;
    }

    public void setArquivo(boolean arquivo) {
        this.arquivo = arquivo;
    }        

    public Long getIdNota() {
        return idNota;
    }

    public void setIdNota(Long idNota) {
        this.idNota = idNota;
    }

    public boolean isPermiteNotaProfissional() {
        return permiteNotaProfissional;
    }

    public void setPermiteNotaProfissional(boolean permiteNotaProfissional) {
        this.permiteNotaProfissional = permiteNotaProfissional;
    }
    
    
}
