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

import br.ufsm.cpd.javatelemed.persistence.model.TipoProfissional;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
public interface TipoProfissionalService extends CrudRepository<TipoProfissional, Long> {

    @Override
    @Query("SELECT DISTINCT tipo FROM TipoProfissional tipo left join fetch tipo.estadosConsulta WHERE tipo.id = :id")
    public Optional<TipoProfissional> findById(@Param("id")Long id);

    @Query("SELECT DISTINCT tipo FROM TipoProfissional tipo WHERE tipo.descricao <> 'Administrador' and (tipo.descricaoConselho is null or  tipo.descricaoConselho = '')")
    public Collection<TipoProfissional> findAllSemConselho();
    

}
