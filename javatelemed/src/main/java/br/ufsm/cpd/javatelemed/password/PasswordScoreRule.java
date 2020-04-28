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

import java.util.Map;

/**
 * Interface que define uma regra de cálculo de força.
 *
 * @author Marcius da Silva da Fonseca (mfonseca@ufsm.br)
 * @version 1.0
 */
public interface PasswordScoreRule extends PasswordRule {

    /**
     * Retorna uma frase curta que dá uma dica para melhorar a força da senha no quesito de cálculo abordado por esta regra.
     *
     * @return Uma dica para melhorar a força da senha no quesito abordado por esta regra.
     */
    public String getScoreMessage();

    /**
     * Retorna o peso da regra no cálculo do score global.
     * <p/>
     * <b>Deve sempre retornar um inteiro maior que 0!</b>
     *
     * @return O peso da regra no score global.
     */
    public int getScoreWeigth();

    /**
     * Calculador de score de força da senha, referente ao quesito abordado por esta regra.
     * <p/>
     * <b>Deve sempre retornar um inteiro positivo entre 0 e 100!</b>
     *
     * @param password A senha a ser avaliada.
     * @param extraParams Parâmetros extras opcionais.
     * @return Um valor positivo entre 0 e 100.
     */
    public int score(final String password, final Map<String, Object> extraParams);
}
