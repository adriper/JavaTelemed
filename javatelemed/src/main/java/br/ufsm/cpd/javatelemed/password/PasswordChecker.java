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

import java.util.Collection;
import java.util.Map;

/**
 * Interface que define o comportamento de um PasswordChecker.
 *
 * @author Marcius da Silva da Fonseca (mfonseca@ufsm.br)
 * @version 1.0
 */
public interface PasswordChecker {

    public static final String GLOBAL_RESULT_KEY = "GlobalResult";

    /**
     * Avalia a senha (validação e calculo da força) em todos os quesitos configurados.
     *
     * @param password A senha a ser avaliada.
     * @param extraParams Parâmetros extras opcionais.
     * @return O resultado da avaliação.
     */
    public Map<String, PasswordResult> evaluate(final String password, final Map<String, String> extraParams);

    /**
     * Retorna o resultado parcial da última avaliação para a regra de id dado.
     * <p/>
     * <b>Se ainda não foi feita nenhuma avaliação, o resultado deverá ser inválido e score = 0</b>
     *
     * @param ruleId O id da regra.
     * @return O resultado parcial da última avaliação.
     */
    public PasswordResult getRuleResult(final String ruleId);

    /**
     * Retorna o resultado global da última avaliação efetuada.
     * <p/>
     * <b>Se ainda não foi feita nenhuma avaliação, o resultado deverá ser inválido e score = 0</b>
     *
     * @return O resultado global da última avaliação.
     */
    public PasswordResult getGlobalResult();

    /**
     * Retorna os resultados da última avaliação de todas as regras deste checker. Também retorna o resultado global sob a chave
     * {@link #GLOBAL_RESULT_KEY}.
     * <p/>
     * <b>Se ainda não foi feita nenhuma avaliação, todos os resultados deverão ser inválidos e score = 0</b>
     *
     * @return O resultado da última avaliação.
     */
    public Map<String, PasswordResult> getResults();

    /**
     * Retorna a coleção completa de regras gerenciadas por este PasswordChecker.
     *
     * @return A coleção de regras gerenciadas por este PasswordChecker.
     */
    public Collection<PasswordRule> getRules();

    /**
     * Retorna a coleção de {@link PasswordValidationRule regras de validação} gerenciadas por este PasswordChecker que estão <b>ativas</b>.
     *
     * @return A coleção de {@link PasswordValidationRule regras de validação} gerenciadas por este PasswordChecker que estão <b>ativas</b>.
     */
    public Collection<PasswordValidationRule> getValidationRules();

    /**
     * Retorna a coleção de {@link PasswordScoreRule regras de score} gerenciadas por este PasswordChecker que estão <b>ativas</b>.
     *
     * @return A coleção de {@link PasswordScoreRule regras de score} gerenciadas por este PasswordChecker que estão <b>ativas</b>.
     */
    public Collection<PasswordScoreRule> getScoreRules();

    /**
     * Reseta todos os resultados, parciais e global, passando-os para inválido e score=0.
     *
     * @return <code>this</code>, para permitir chamadas em cadeia.
     */
    public PasswordChecker reset();
}
