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
import br.ufsm.cpd.javatelemed.password.PasswordScoreRule;
import br.ufsm.cpd.javatelemed.password.PasswordValidationRule;
import java.util.Map;

/**
 * Regra que avalia a presença de letras minúsculas na senha.
 * <p/>
 * Se houver alguma letra minúscula, melhor.
 * <p/>
 * Suporta {@link PasswordValidationRule validação} e {@link PasswordScoreRule score} de senhas.
 *
 * @author Marcius da Silva da Fonseca (mfonseca@ufsm.br)
 * @version 1.0
 */
public class LowercaseRule extends AbstractPasswordRule implements PasswordValidationRule, PasswordScoreRule {

    private static final long serialVersionUID = -578875518867655699L;

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean validate(final String password, final Map<String, Object> extraParams) {
        return TextUtils.hasLowercase(password);
    }

    /**
     * {@inheritDoc }
     * <p/>
     * Avalia a presença de letras minúsculas na senha.
     * <p/>
     * Se houver alguma letra minúscula, melhor.
     */
    @Override
    public int score(final String password, final Map<String, Object> extraParams) {
        return TextUtils.hasLowercase(password) ? 100 : 0;
    }
}
