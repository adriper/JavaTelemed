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
package br.ufsm.cpd.javatelemed.persistence.model;

import br.ufsm.cpd.javatelemed.commons.persistence.Entidade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.core.instrument.util.StringUtils;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

/**
 *
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
@Entity
public class TipoProfissional extends Entidade {

    private static final long serialVersionUID = 8971263987351832150L;
    @Id
    @Column(name = "id_tipo_profissional")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String descricao;
    private String descricaoConselho;
    private Boolean prescricao;
    private Boolean videoconferencia;
    private String siglaConselho;
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "estado_consulta_tipo_profissional",
            joinColumns = @JoinColumn(name = "id_tipo_profissional"),
            inverseJoinColumns = @JoinColumn(name = "id_estado_consulta"))
    private Set<EstadoConsulta> estadosConsulta;

    @Override
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

    public String getDescricaoConselho() {
        return descricaoConselho;
    }

    public void setDescricaoConselho(String descricaoConselho) {
        this.descricaoConselho = descricaoConselho;
    }

    public String getSiglaConselho() {
        return siglaConselho;
    }

    public void setSiglaConselho(String siglaConselho) {
        this.siglaConselho = siglaConselho;
    }

    public Set<EstadoConsulta> getEstadosConsulta() {
        return estadosConsulta;
    }

    public void setEstadosConsulta(Set<EstadoConsulta> estadosConsulta) {
        this.estadosConsulta = estadosConsulta;
    }        

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.descricao);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TipoProfissional other = (TipoProfissional) obj;
        return Objects.equals(this.descricao, other.descricao);
    }
    
    public Boolean podeSerPreceptor(){
        return StringUtils.isNotBlank(getDescricaoConselho());
    }
    public Boolean getPodeSerPreceptor(){
        return podeSerPreceptor();
    }

    public Boolean getPrescricao() {
        return prescricao;
    }

    public void setPrescricao(Boolean prescricao) {
        this.prescricao = prescricao;
    }

    public Boolean getVideoconferencia() {
        return videoconferencia;
    }

    public void setVideoconferencia(Boolean videoconferencia) {
        this.videoconferencia = videoconferencia;
    }
    
    
}
