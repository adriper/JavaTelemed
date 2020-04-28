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
 * Interface que define uma regra de validação.
 *
 * @author Marcius da Silva da Fonseca (mfonseca@ufsm.br)
 * @version 1.0
 */
public interface PasswordValidationRule extends PasswordRule {

    /**
     * Retorna uma frase curta que representa o quesito de validação abordado por esta regra.
     *
     * @return Uma frase representando o quesito de validação abordado por esta regra.
     */
    public String getValidationMessage();

    /**
     * Retorna uma frase de erro da validação da regra.
     *
     * @return Uma frase de erro da validação da regra.
     */
    public String getValidationError();

    /**
     * Valida a senha, no quesito abordado por esta regra.
     *
     * @param password A senha a ser avaliada.
     * @param extraParams Parâmetros extras opcionais.
     * @return true, se a senha for válida. false, caso contrário.
     */
    public boolean validate(final String password, final Map<String, Object> extraParams);
}
