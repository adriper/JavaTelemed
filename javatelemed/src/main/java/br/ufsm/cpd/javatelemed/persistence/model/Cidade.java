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
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.DynamicUpdate;

/**
 *
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
@Entity
@DynamicUpdate
public class Cidade extends Entidade {

    private static final long serialVersionUID = 3897123698126307653L;

    @Id
    @Column(name = "id_cidade")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String codigo;
    @NotBlank
    private String nome;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;
    @NotNull
    @Column(name = "ind_atendimento")
    private Boolean atendimento;
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "cidade_atendidas",
            joinColumns = @JoinColumn(name = "id_cidade_sede"),
            inverseJoinColumns = @JoinColumn(name = "id_cidade"))
    private Set<Cidade> cidadesAtendidas;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Boolean getAtendimento() {
        return atendimento;
    }

    public void setAtendimento(Boolean atendimento) {
        this.atendimento = atendimento;
    }

    public Set<Cidade> getCidadesAtendidas() {
        return cidadesAtendidas;
    }

    public void setCidadesAtendidas(Set<Cidade> cidadesAtendidas) {
        this.cidadesAtendidas = cidadesAtendidas;
    }
    
    public String getNomeSiglaEstado(){
        return nome + "/" + estado.getSigla();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.codigo);
        hash = 29 * hash + Objects.hashCode(this.estado);
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
        final Cidade other = (Cidade) obj;
        return Objects.equals(this.codigo, other.codigo) && Objects.equals(this.estado, other.estado);
    }

}
