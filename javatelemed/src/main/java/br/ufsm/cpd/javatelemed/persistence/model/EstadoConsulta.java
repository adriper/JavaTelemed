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

import br.ufsm.cpd.javatelemed.commons.collection.CollectionUtils;
import br.ufsm.cpd.javatelemed.commons.persistence.Entidade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Entity
public class EstadoConsulta extends Entidade {

    private static final long serialVersionUID = -7067056393034764770L;

    @Id
    @Column(name = "id_estado_consulta")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String descricao;
    @JsonIgnore
    @ElementCollection
    @CollectionTable(
            name = "indicador_estado_consulta",
            joinColumns = @JoinColumn(name = "id_estado_consulta"))
    @Column(name = "indicador")
    @Enumerated(EnumType.STRING)
    private Set<IndicadorEstado> indicadores;
    @JsonIgnore
    @OneToMany
    @JoinTable(
            name = "estado_consulta_proximos_estados",
            joinColumns = @JoinColumn(name = "id_estado_consulta"),
            inverseJoinColumns = @JoinColumn(name = "id_proximo_estado"))
    private Set<EstadoConsulta> proximosEstadosPossiveis;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_proximo_estado_paciente")
    private EstadoConsulta proximoEstadoPaciente;
    @JsonIgnore
    private String termoEsclarecimento;

    public EstadoConsulta() {
    }

    public EstadoConsulta(final String descricao) {
        this.descricao = descricao;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(final String descricao) {
        this.descricao = descricao;
    }

    public Set<IndicadorEstado> getIndicadores() {
        return indicadores;
    }

    public void setIndicadores(Set<IndicadorEstado> indicadores) {
        this.indicadores = indicadores;
    }

    public Set<EstadoConsulta> getProximosEstadosPossiveis() {
        return proximosEstadosPossiveis;
    }

    public void setProximosEstadosPossiveis(Set<EstadoConsulta> proximosEstadosPossiveis) {
        this.proximosEstadosPossiveis = proximosEstadosPossiveis;
    }

    public EstadoConsulta getProximoEstadoPaciente() {
        return proximoEstadoPaciente;
    }

    public String getTermoEsclarecimento() {
        return termoEsclarecimento;
    }

    public void setTermoEsclarecimento(String termoEsclarecimento) {
        this.termoEsclarecimento = termoEsclarecimento;
    }

    public void setProximoEstadoPaciente(EstadoConsulta proximoEstadoPaciente) {
        this.proximoEstadoPaciente = proximoEstadoPaciente;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.descricao);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EstadoConsulta other = (EstadoConsulta) obj;
        if (!Objects.equals(this.descricao, other.descricao)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public boolean getPermiteNotaProfissional() {
        return CollectionUtils.isNotEmpty(indicadores) && indicadores.contains(IndicadorEstado.CHAT_PROFISSIONAL);
    }

    public boolean getEnviarTermoEsclarecido() {
        return CollectionUtils.isNotEmpty(indicadores) && indicadores.contains(IndicadorEstado.TERMO_ESCLARECIDO);
    }

    public boolean getProximoEstadoPacienteNotaProfissional() {
        return CollectionUtils.isNotEmpty(indicadores) && indicadores.contains(IndicadorEstado.PRXM_ESTADO_PACIENTE_PROF_NOTA);
    }

    public boolean getProximoEstadoNotaProfissional() {
        return CollectionUtils.isNotEmpty(indicadores) && indicadores.contains(IndicadorEstado.PROXIMO_ESTADO_PROF_NOTA);
    }

    public boolean getPermiteNotaPaciente() {
        return CollectionUtils.isNotEmpty(indicadores) && indicadores.contains(IndicadorEstado.CHAT_PACIENTE);
    }

    public boolean getPermiteConsideracoesProfissional() {
        return CollectionUtils.isNotEmpty(indicadores) && indicadores.contains(IndicadorEstado.PERMITE_CONSIDERACAO_PROF);
    }

    public boolean getAtendimentoFinalizado() {
        return CollectionUtils.isNotEmpty(indicadores) && indicadores.contains(IndicadorEstado.ESTADO_FINAL);
    }

    public boolean getPermiteAlterarProfissional() {
        return (CollectionUtils.isNotEmpty(indicadores) && indicadores.contains(IndicadorEstado.ALTERAR_PROFISSIONAL));
    }

    public boolean proximoEstadoProfissionalAbre() {
        return CollectionUtils.isNotEmpty(indicadores) && indicadores.contains(IndicadorEstado.PROXIMO_ESTADO_PROF_ABRE);
    }

    public boolean isEstadoPaciente() {
        return CollectionUtils.isNotEmpty(indicadores) && indicadores.contains(IndicadorEstado.ESTADO_PACIENTE);
    }

    public boolean getEstadoInicial() {
        return CollectionUtils.isNotEmpty(indicadores) && indicadores.contains(IndicadorEstado.ESTADO_INICIAL);
    }

    public boolean getEnviaEmailPaciente() {
        return CollectionUtils.isNotEmpty(indicadores) && indicadores.contains(IndicadorEstado.EMAIL_PCTE_TROCA_ESTADO);
    }

}
