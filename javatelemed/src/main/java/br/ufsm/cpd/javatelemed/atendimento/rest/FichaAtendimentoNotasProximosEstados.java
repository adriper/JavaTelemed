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

import br.ufsm.cpd.javatelemed.persistence.model.EstadoConsulta;
import br.ufsm.cpd.javatelemed.persistence.model.NotaAtendimento;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
public class FichaAtendimentoNotasProximosEstados implements Serializable {

    private Collection<NotaAtendimento> notas;
    private Collection<EstadoConsulta> proximosEstados;

    public Collection<NotaAtendimento> getNotas() {
        return notas;
    }

    public void setNotas(Collection<NotaAtendimento> notas) {
        this.notas = notas;
    }

    public Collection<EstadoConsulta> getProximosEstados() {
        return proximosEstados;
    }

    public void setProximosEstados(Collection<EstadoConsulta> proximosEstados) {
        this.proximosEstados = proximosEstados;
    }

    public FichaAtendimentoNotasProximosEstados() {
    }

    public FichaAtendimentoNotasProximosEstados(Collection<NotaAtendimento> notas, Collection<EstadoConsulta> proximosEstados) {
        this.notas = notas;
        this.proximosEstados = proximosEstados;
    }

}
