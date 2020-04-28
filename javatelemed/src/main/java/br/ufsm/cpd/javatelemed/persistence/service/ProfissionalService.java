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
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.model.TipoProfissional;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 *
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
public interface ProfissionalService extends CrudRepository<Profissional, Long>, UserDetailsService {

    Optional<Profissional> findByCpf(final String cpf);

    Optional<Profissional> findByEmail(final String email);

    @Override
    @Query("SELECT p from Profissional p LEFT JOIN FETCH p.preceptoresResponsaveis responsaveis LEFT JOIN FETCH responsaveis.cidadesSede cidadesSedePreceptor LEFT JOIN FETCH p.cidadesSede INNER JOIN FETCH p.tipoProfissional tipo LEFT JOIN FETCH tipo.estadosConsulta WHERE p.id = :id")
    public Optional<Profissional> findById(@Param("id") Long id);

    @Query("SELECT distinct p FROM Profissional p JOIN FETCH p.cidadesSede cidades WHERE p.tipoProfissional = :tipoProfissional AND cidades = :cidadeSede AND"
            + " NOT EXISTS (SELECT 1 FROM ProfissionalPreceptor pp WHERE pp.aluno.id = p.id AND pp.preceptor = :preceptor AND pp.validade > current_timestamp() AND pp.ativo IS TRUE)")
    public Collection<Profissional> findByTipoProfissionalCidadeSede(@Param("tipoProfissional") final TipoProfissional tipoProfissional, @Param("cidadeSede") final Cidade cidadeSede, @Param("preceptor") final Profissional preceptor);

    @Override
    default UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        return (UserDetails) findByEmail(email).orElse(null);
    }
}
