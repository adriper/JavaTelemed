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
package br.ufsm.cpd.javatelemed.web.rest;

import br.ufsm.cpd.javatelemed.persistence.model.Cidade;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 *
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
@RepositoryRestResource(path = "cidade", itemResourceRel = "cidade", collectionResourceRel = "cidades")
public interface CidadeRestController extends PagingAndSortingRepository<Cidade, Long> {

    Cidade findByCodigo(final String codigo);
}
