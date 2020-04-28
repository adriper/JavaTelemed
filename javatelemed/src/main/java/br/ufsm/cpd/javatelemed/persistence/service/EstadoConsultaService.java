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
package br.ufsm.cpd.javatelemed.persistence.service;

import br.ufsm.cpd.javatelemed.persistence.model.EstadoConsulta;
import br.ufsm.cpd.javatelemed.persistence.model.IndicadorEstado;
import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
public interface EstadoConsultaService extends CrudRepository<EstadoConsulta, Long> {

    public static final String DESCRICAO_ESTADO_NOVO = "Novo";

    public Page<EstadoConsulta> findAll(final Pageable pageable);
    @Query("SELECT e FROM EstadoConsulta e ORDER BY :order")
    public Collection<EstadoConsulta> findOrder(@Param("order") final String order);
    
    public EstadoConsulta findByDescricao(final String descricao);

    public default EstadoConsulta findInicial() {
//        return findByDescricao(DESCRICAO_ESTADO_NOVO);
        return findByIndicadores(IndicadorEstado.INICIO_CLINICA);
    }
    
    public EstadoConsulta findByIndicadores(final IndicadorEstado indicador);

}
