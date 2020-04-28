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
 * Regra que avalida a diversidade de tipos de caracteres encontrados na senha.
 * <p/>
 * Qto mais tipos (minúscula|maiúscula|dígito|símbolo|espaço) de caracteres, melhor.
 *
 * @author Marcius da Silva da Fonseca (mfonseca@ufsm.br)
 * @version 1.0
 */
public class CharTypeDiversityRule extends AbstractPasswordRule implements PasswordValidationRule, PasswordScoreRule {

    private static final long serialVersionUID = -5312771126565151822L;
    private static final int TOTAL_EVALUATED_TYPES = 5;
    /**
     * Indica a quantidade mínima de classes de caracteres aceita.
     */
    private int minTypeDiversity;

    /**
     * Construtor padrão.
     */
    public CharTypeDiversityRule() {
        this(2);
    }

    /**
     * Construtor que indica a quantidade mínima de classes de caracteres aceita.
     *
     * @param minTypeDiversity A quantidade mínima de classes de caracteres aceita.
     */
    public CharTypeDiversityRule(final int minTypeDiversity) {
        super();
        this.minTypeDiversity = minTypeDiversity;
        setScoreWeigth(2); // aumenta a importancia desse score.
    }

    /**
     * Retorna a quantidade mínima de classes de caracteres aceita.
     *
     * @return A quantidade mínima de classes de caracteres aceita.
     */
    public int getMinTypeDiversity() {
        return minTypeDiversity;
    }

    /**
     * Indica a quantidade mínima de classes de caracteres aceita.
     *
     * @param minTypeDiversity A quantidade mínima de classes de caracteres aceita.
     */
    public void setMinTypeDiversity(final int minTypeDiversity) {
        this.minTypeDiversity = minTypeDiversity;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getValidationMessage() {
        return MessageFormat.format(super.getValidationMessage(), minTypeDiversity);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean validate(final String password, final Map<String, Object> extraParams) {
        return minTypeDiversity <= countDiversity(password)[0];
    }

    /**
     * {@inheritDoc }
     * <p/>
     * Avalida a diversidade de tipos de caracteres encontrados na senha.
     * <p/>
     * Qto mais tipos de caracteres, melhor.
     */
    @Override
    public int score(final String password, final Map<String, Object> extraParams) {
        int[] count = countDiversity(password);
        int pass = 100 / count[1];
        return count[0] * pass;
    }

    /**
     * Retorna um vetor de duas posições, onde a primeira posição indica qtos tipos foram encontrados e a segunda posição indica a quantidade de tipos
     * existente/avaliados.
     *
     * @param password A senha a ser avaliada.
     * @return um vetor de duas posições contendo [found, max] classes para a senha dada.
     */
    protected int[] countDiversity(final String password) {
        int count[] = {0, TOTAL_EVALUATED_TYPES}; // [found, max]
        if (TextUtils.hasLowercase(password)) {
            count[0]++;
        }
        if (TextUtils.hasUppercase(password)) {
            count[0]++;
        }
        if (TextUtils.hasDigit(password)) {
            count[0]++;
        }
        if (TextUtils.hasSymbol(password)) {
            count[0]++;
        }
        if (TextUtils.hasSpace(password)) {
            count[0]++;
        }
        return count;
    }
}
