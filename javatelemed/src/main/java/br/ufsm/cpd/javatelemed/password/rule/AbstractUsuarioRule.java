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
package br.ufsm.cpd.javatelemed.password.rule;

import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalService;
import br.ufsm.cpd.javatelemed.service.RecuperacaoSenhaService;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Giuliano Ferreira
 */
public class AbstractUsuarioRule extends AbstractPasswordRule {

    private static final long serialVersionUID = 3058168708945493562L;
    @Autowired
    protected transient ProfissionalService profissionalService;
    @Autowired
    protected transient RecuperacaoSenhaService recuperacaoSenhaService;

    protected Profissional getUsuario(final Map<String, Object> extraParams) {
        final Profissional profissional = Optional.ofNullable((Profissional) extraParams.get("profissional")).orElseGet(() -> {
            if (extraParams.containsKey("email")) {
                return profissionalService.findByEmail((String) extraParams.get("email")).orElse(null);
            } else if (extraParams.containsKey("cpf")) {
                return profissionalService.findByCpf((String) extraParams.get("cpf")).orElse(null);
            } else if (extraParams.containsKey("token")) {
                try {
                    return recuperacaoSenhaService.validaToken((String) extraParams.get("token"));
                } catch (Exception ex) {
                    return null;
                }
            } else {
                return null;
            }
        });
        extraParams.putIfAbsent("profissional", profissional);
        return Optional.ofNullable(profissional).orElseThrow(() -> new IllegalStateException("Usuário não encontrado"));
    }
}
