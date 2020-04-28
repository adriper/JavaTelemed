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
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Entity
public class ProfissionalPreceptor extends Entidade {

    @Id
    @Column(name = "id_profissional_preceptor")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id_preceptor")
    private Profissional preceptor;
    @ManyToOne
    @JoinColumn(name = "id_aluno")
    private Profissional aluno;
    @Temporal(TemporalType.TIMESTAMP)
    private Date validade;
    @Temporal(TemporalType.TIMESTAMP)
    private Date inicio;
    private Boolean ativo;

    public Profissional getPreceptor() {
        return preceptor;
    }

    public void setPreceptor(Profissional preceptor) {
        this.preceptor = preceptor;
    }

    public Profissional getAluno() {
        return aluno;
    }

    public void setAluno(Profissional aluno) {
        this.aluno = aluno;
    }

    public Date getValidade() {
        return validade;
    }

    public void setValidade(Date validade) {
        this.validade = validade;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }        

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.id);
        hash = 47 * hash + Objects.hashCode(this.preceptor);
        hash = 47 * hash + Objects.hashCode(this.aluno);
        hash = 47 * hash + Objects.hashCode(this.validade);
        hash = 47 * hash + Objects.hashCode(this.ativo);
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
        final ProfissionalPreceptor other = (ProfissionalPreceptor) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.preceptor, other.preceptor)) {
            return false;
        }
        if (!Objects.equals(this.aluno, other.aluno)) {
            return false;
        }
        if (!Objects.equals(this.validade, other.validade)) {
            return false;
        }
        if (!Objects.equals(this.ativo, other.ativo)) {
            return false;
        }
        return true;
    }
    
    

}
