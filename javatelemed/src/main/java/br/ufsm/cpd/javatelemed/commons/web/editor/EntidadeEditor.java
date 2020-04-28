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
package br.ufsm.cpd.javatelemed.commons.web.editor;

import br.ufsm.cpd.javatelemed.commons.persistence.Entidade;
import java.beans.PropertyEditorSupport;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 * Editor de entidades.
 *
 * @author mfonseca
 * @param <E> Tipo de entidade.
 */
public class EntidadeEditor<E extends Entidade> extends PropertyEditorSupport {

    private final CrudRepository<E, Long> service;

    public EntidadeEditor(final CrudRepository<E, Long> service) {
        this.service = service;
    }

    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        final Optional<E> v = Optional.ofNullable(text)
                .map(t -> t.trim())
                .map(t -> !t.isEmpty() ? Long.valueOf(t) : null)
                .map(id -> service.findById(id))
                .orElse(null);
        setValue(v);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getAsText() {
        return Optional.ofNullable((E) getValue())
                .map(e -> e.getId())
                .map(id -> id.toString())
                .orElse(null);
    }
}
