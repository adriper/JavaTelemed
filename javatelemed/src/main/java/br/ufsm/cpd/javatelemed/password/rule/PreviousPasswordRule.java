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

import br.ufsm.cpd.javatelemed.commons.text.TextUtils;
import br.ufsm.cpd.javatelemed.password.PasswordValidationRule;
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Regra que avalia se a senha é manjada (bloqueada).
 *
 * Suporta apenas {@link PasswordValidationRule validação} de senhas.
 *
 * @author Marcius da Silva da Fonseca (mfonseca@ufsm.br)
 * @version 1.0
 */
@Component
public class PreviousPasswordRule extends AbstractUsuarioRule implements PasswordValidationRule {

    private static final long serialVersionUID = -8217508856913825108L;
    @Autowired
    private transient PasswordEncoder passwordEncoder;

    public PreviousPasswordRule() {
        super();
        setScoreWeigth(4);
    }

    /**
     * {@inheritDoc }
     *
     * Implementa uma regra de validação que bloqueia o uso do login como senha.
     */
    @Override
    public boolean validate(final String password, final Map<String, Object> extraParams) {
        if (TextUtils.isBlankOrNull(password)) {
            return false;
        }
        final Profissional usuario = getUsuario(extraParams);
        return !passwordEncoder.matches(password, usuario.getSenha());
    }
}
