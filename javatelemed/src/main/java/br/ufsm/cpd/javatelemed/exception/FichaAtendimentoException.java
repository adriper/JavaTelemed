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
package br.ufsm.cpd.javatelemed.exception;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
public class FichaAtendimentoException extends Exception {

    public enum TipoAtendeFichaException {
        CONCLUIDO, OUTRO_PROFISSIONAL, CIDADE_SEDE, ESTADO_CONSULTA,
        RESPONSAVEL, RESPONSAVEL_NULL_ESTADO_SEM_TROCA
    };

    private TipoAtendeFichaException tipoAtendeFichaException;

    public TipoAtendeFichaException getTipoAtendeFichaException() {
        return tipoAtendeFichaException;
    }

    public void setTipoAtendeFichaException(TipoAtendeFichaException tipoAtendeFichaException) {
        this.tipoAtendeFichaException = tipoAtendeFichaException;
    }

    public FichaAtendimentoException() {
    }

    public FichaAtendimentoException(TipoAtendeFichaException tipoAtendeFichaException) {
        this.tipoAtendeFichaException = tipoAtendeFichaException;
    }

    @Override
    public String getMessage() {
        switch (tipoAtendeFichaException) {
            case CONCLUIDO:
                return "O atendimento já foi concluído";
            case CIDADE_SEDE:
                return "Esta ficha pertence a uma cidade sede que você não tem acesso";
            case OUTRO_PROFISSIONAL:
                return "Este atendimento já está sendo realizado por outro profissional, e não é possível trocá-lo";
            case ESTADO_CONSULTA:
                return "Você não possui permissão para acessar esta ficha de atendimento, devido ao estado do atendimento";
            case RESPONSAVEL:
                return "Você não pode ser responsável por uma ficha sem estar vinculado a um preceptor";
            case RESPONSAVEL_NULL_ESTADO_SEM_TROCA:
                return "Esta ficha não possui responsável, porém o estado atual não permite a vinculação de um profissional. Entre em contato com os administradores do sistema.";
            default:
                return "Ocorreu um erro inesperado";
        }
    }

}
