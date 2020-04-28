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
package br.ufsm.cpd.javatelemed.password;

import br.ufsm.cpd.javatelemed.commons.text.TextBuilder;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Classe que guarda os resultados de uma avaliação, seja ela global ou parcial.
 *
 * @author Marcius da Silva da Fonseca (mfonseca@ufsm.br)
 * @version 1.0
 */
public class PasswordResult implements Serializable {

    private static final long serialVersionUID = 7896205009951039131L;
    /**
     * Scores maiores ou iguais a isso serão considerados aceitáveis. Scores menores serão considerados fracos.
     */
    protected static final int ACCEPTABLE_THRESHOLD = 40;
    /**
     * Scores maiores ou iguais a isso serão considerados fortes.
     */
    protected static final int STRONG_THRESHOLD = 70;
    /**
     * A regra que gerou o resultado.
     */
    private final PasswordRule rule;
    /**
     * Guarda o resultado da validação.
     */
    private boolean valid;
    /**
     * Guarda o resultado do cálculo do score da força.
     */
    private int score;
    /**
     * O peso do score desse resultado no score global.
     */
    private int scoreWeigth;

    /**
     * Por default, <code>valid = false</code> e <code>score = 0</code>
     *
     */
    public PasswordResult() {
        this(null);
    }

    /**
     * Por default, <code>valid = false</code> e <code>score = 0</code>
     *
     * @param rule A regra que gerou este resultado. Nulo significa "resultado global".
     */
    public PasswordResult(final PasswordRule rule) {
        this.valid = false;
        this.score = 0;
        this.rule = rule;
    }

    /**
     * Retorna a regra que gerou este resultado.
     *
     * @return a regra que gerou este resultado.
     */
    public PasswordRule getRule() {
        return rule;
    }

    /**
     * Retorna o id da regra que gerou este resultado
     *
     * @return O id da regra que gerou este resultado
     */
    public String getRuleId() {
        return Optional.ofNullable(rule).map(r -> r.getId()).orElse(PasswordChecker.GLOBAL_RESULT_KEY);
    }

    /**
     * Indica o resultado da validação da senha.
     *
     * @return true, se senha válida. false caso contrário.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Indica o resultado da validação da senha.
     *
     * @param valid O indicador se a senha é válida ou não.
     */
    public void setValid(final boolean valid) {
        this.valid = valid;
    }

    /**
     * Indica o resultado do score da força da senha.
     *
     * @return Um número positivo, entre 0 e 100.
     */
    public int getScore() {
        return score;
    }

    /**
     * Indica o resultado do score da força da senha.
     *
     * @param score O score da senha, devendo ser sempre um número positivo, entre 0 e 100.
     */
    public void setScore(final int score) {
        this.score = score;
    }

    /**
     * Retorna o peso do score desse resultado no score global.
     *
     * @return O peso do score desse resultado no score global.
     */
    public int getScoreWeigth() {
        return scoreWeigth;
    }

    /**
     * Indica o peso do score desse resultado no score global.
     *
     * @param scoreWeigth O peso do score desse resultado no score global.
     */
    public void setScoreWeigth(final int scoreWeigth) {
        this.scoreWeigth = scoreWeigth;
    }

    /**
     * Indica se este resultado é referente a avaliação global de um PasswordChecker.
     *
     * @return
     */
    public boolean isGlobal() {
        return PasswordChecker.GLOBAL_RESULT_KEY.equals(getRuleId());
    }

    /**
     * Indica se este resultado é referente a avaliação uma PasswordRule específica.
     *
     * @return
     */
    public boolean isParcial() {
        return !isGlobal();
    }

    /**
     * É apenas a negação do {@link #isValid() isValid()}. Facilita a leitura do código.
     *
     * @return true, se senha INválida. false caso contrário.
     */
    public boolean isNotValid() {
        return !isValid();
    }

    /**
     * Indica se o score calculado é fraco.
     *
     * Util para scores globais.
     *
     * @return true, se <code>(score &lt; ACCEPTABLE_THRESHOLD)</code>
     */
    public boolean isWeak() {
        return this.score < ACCEPTABLE_THRESHOLD;
    }

    /**
     * Indica se o score calculado é razoável.
     *
     * Util para scores globais.
     *
     * @return true, se <code>(ACCEPTABLE_THRESHOLD &lt;= score &lt; STRONG_THRESHOLD)</code>
     */
    public boolean isAcceptable() {
        return this.score >= ACCEPTABLE_THRESHOLD && score < STRONG_THRESHOLD;
    }

    /**
     * Indica se o score calculado é forte.
     *
     * Util para scores globais.
     *
     * @return true, se <code>(score &gt;= STRONG_THRESHOLD)</code>
     */
    public boolean isStrong() {
        return this.score >= STRONG_THRESHOLD;
    }

    /**
     * Reseta os valores para <code>valid = false</code> e <code>score = 0</code>.
     *
     * @return <code>this</code>, para permitir chamada em cadeia.
     */
    public PasswordResult reset() {
        this.valid = false;
        this.score = 0;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.rule);
        hash = 59 * hash + (this.valid ? 1 : 0);
        hash = 59 * hash + this.score;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PasswordResult other = (PasswordResult) obj;
        if (this.valid != other.valid) {
            return false;
        }
        if (this.score != other.score) {
            return false;
        }
        return Objects.equals(this.rule, other.rule);
    }

    @Override
    public String toString() {
        final TextBuilder builder = new TextBuilder();
        builder.append("Result for ").append(this.getRuleId()).
                append(" { valid? ").append(isValid(), () -> "yes", () -> "no").
                append(" | score: ").append(getScore()).
                appendIf(isWeak(), () -> " (Weak)").
                appendIf(isAcceptable(), () -> " (Acceptable)").
                appendIf(isStrong(), () -> " (Strong)").
                append(" | scoreWeigth: ").append(getScoreWeigth()).
                append(" }");
        return builder.toString();
    }
}
