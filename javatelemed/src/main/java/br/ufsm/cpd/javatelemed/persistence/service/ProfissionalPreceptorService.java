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

import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.model.ProfissionalPreceptor;
import java.util.Collection;
import java.util.Date;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
public interface ProfissionalPreceptorService extends CrudRepository<ProfissionalPreceptor, Long> {

    @Query("select count (distinct pp.id) from ProfissionalPreceptor pp where pp.aluno = :aluno and pp.ativo is true and "
            + "((:inicio >= pp.inicio and :inicio <= pp.validade) or"
            + "(:fim >= pp.inicio and :fim <= pp.validade) or "
            + "(pp.inicio >= :inicio and pp.inicio <= :fim) or"
            + "(pp.validade >= :inicio and pp.validade <= :fim) or "
            + "(pp.inicio <= :inicio and pp.validade >= :fim) or "
            + "(:inicio <= pp.inicio and :fim >= pp.validade)"
            + ")")
    public abstract Number countByEstudanteInicioFim(@Param("aluno") final Profissional aluno, @Param("inicio") final Date inicio, @Param("fim") final Date fim);

}
