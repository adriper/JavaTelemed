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

import br.ufsm.cpd.javatelemed.persistence.model.Cidade;
import br.ufsm.cpd.javatelemed.persistence.model.Estado;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
public interface CidadeService extends CrudRepository<Cidade, Long> {

    Collection<Cidade> findByEstado(final Estado estado);

    @Query("select c from Cidade c where c.atendimento = true and c.estado.id = :idEstado order by c.nome")
    Collection<Cidade> findCidadesSede(final Long idEstado);

    @Query("select c from Cidade c where c.atendimento is true order by c.nome")
    Collection<Cidade> findCidadesSede();

    @Query("select c from Cidade c where c.atendimento = true and c.estado.sigla = :sigla order by c.nome")
    Collection<Cidade> findCidadesSedeBySiglaEstado(@Param("sigla") final String sigla);

    @Query("select distinct c.estado from Cidade c where c.atendimento = true order by c.estado.nome")
    Collection<Estado> findEstadosComCidadeSede();

    @Query("select distinct c from Cidade c where c.estado = :estado and upper(c.nome) like :nome order by c.nome")
    public Page<Cidade> findByEstadoNome(@Param("estado") final Estado estado, @Param("nome") final String nome, final Pageable pageable);
}
