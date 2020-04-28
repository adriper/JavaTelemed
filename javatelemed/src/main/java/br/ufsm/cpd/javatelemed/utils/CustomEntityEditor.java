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
package br.ufsm.cpd.javatelemed.utils;

import br.ufsm.cpd.javatelemed.commons.persistence.Entidade;
import java.beans.PropertyEditorSupport;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Deprecated
public class CustomEntityEditor<T extends Entidade> extends PropertyEditorSupport {

    private CrudRepository<T, Long> service;

    @Override
    public String getAsText() {
        Entidade e = (Entidade) getValue();
        if (e != null) {
            final Long id = e.getId();
            return id.toString();
        } else {
            return null;
        }
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        final long id = Long.parseLong(text.trim());
        final Optional<T> entidade = service.findById(id);
        if (entidade.isPresent()) {
            setValue(entidade);
        } else {
            setValue(null);
        }
    }

    public CustomEntityEditor(CrudRepository<T, Long> service) {
        this.service = service;
    }

}
