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

import java.io.Serializable;
import java.util.Map;

/**
 * Interface base para todas as regras de senha.
 *
 * @author Marcius da Silva da Fonseca (mfonseca@ufsm.br)
 * @version 1.0
 */
public interface PasswordRule extends Serializable {

    /**
     * Retorna um id único, para identificar o tipo da regra.
     *
     * @return O id único que identifica o tipo da regra.
     */
    public String getId();

    /**
     * Indica se esta regra suporta validação.
     *
     * @return true, se a regra implementa validação. false caso contrário.
     */
    public boolean isValidationSupported();

    /**
     * Indica se esta regra suporta score.
     *
     * @return true, se a regra implementa score. false caso contrário.
     */
    public boolean isScoreSupported();

    /**
     * Indica se a validação está ativa.
     *
     * @return true, se a validação implementada na regra (se houver) deverá ser chamada pelo checker. false, se for para ignorar a validação.
     */
    public boolean isValidationActive();

    /**
     * Indica se o cálculo de score está ativo.
     *
     * @return true, se o cálculo de score implementado na regra (se houver) deverá ser chamado pelo checker. false, se for para ignorar o cálculo.
     */
    public boolean isScoreActive();

    /**
     * Avalia a senha no quesito da regra corrente.
     *
     * @param password A senha a ser avaliada.
     * @param extraParams Parâmetros extras opcionais.
     * @return O resultado da avaliação.
     */
    public PasswordResult evaluate(final String password, final Map<String, Object> extraParams);
}
