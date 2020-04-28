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
import java.text.MessageFormat;
import java.util.Map;

/**
 * Regra que avalida o tamanho da senha.
 *
 * Qto mais caracteres, melhor (até o limite max).
 *
 * Suporta {@link PasswordValidationRule validação} e {@link PasswordScoreRule score} de senhas.
 *
 * @author Marcius da Silva da Fonseca (mfonseca@ufsm.br)
 * @version 1.0
 */
public class LengthRule extends AbstractPasswordRule implements PasswordValidationRule, PasswordScoreRule {

    private static final long serialVersionUID = 2273413707031291743L;
    private int minLength;
    private int maxLength;

    public LengthRule() {
        this(6, 20);
    }

    public LengthRule(final int minLength, final int maxLength) {
        super();
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public void setMinLength(final int minLength) {
        this.minLength = minLength;
    }

    public void setMaxLength(final int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getValidationMessage() {
        return MessageFormat.format(super.getValidationMessage(), minLength, maxLength);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getValidationError() {
        return MessageFormat.format(super.getValidationError(), minLength, maxLength);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean validate(final String password, final Map<String, Object> extraParams) {
        final int length = TextUtils.length(password);
        return length >= minLength && length <= maxLength;
    }

    /**
     * {@inheritDoc }
     * Avalida o tamanho da senha.
     *
     * Qto mais caracteres, melhor (até o limite max).
     */
    @Override
    public int score(final String password, final Map<String, Object> extraParams) {
        int max = maxLength - minLength + 1;
        int pass = max > 0 ? 100 / max : 0;
        int count = TextUtils.length(password) - minLength;
        return count > 0 ? count * pass : 0;
    }
}
