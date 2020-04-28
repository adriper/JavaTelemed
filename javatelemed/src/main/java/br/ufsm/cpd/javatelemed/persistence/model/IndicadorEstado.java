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
package br.ufsm.cpd.javatelemed.persistence.model;

/**
 *
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
public enum IndicadorEstado {
    ESTADO_INICIAL("Inicio do atendimento"),
    ESTADO_FINAL("Atendimento finalizado"),
    CHAT_PROFISSIONAL("Permite interação do profissional"),
    CHAT_PACIENTE("Permite interação do paciente"),
    ALTERAR_PROFISSIONAL("Permite alteração do profissional"),
    TERMO_ESCLARECIDO("Envia termo de livre esclarecido"),
    PROXIMO_ESTADO_PROF_ABRE("Vai para o próximo estado quando o profissional abre"),
    PROXIMO_ESTADO_PROF_NOTA("Vai para o próximo estado quando o profissional envia uma nota"),
    PRXM_ESTADO_PACIENTE_PROF_NOTA("Vai para o próximo estado do paciente quando o profissional envia uma nota"),
    PROXIMO_ESTADO_PCT_NOTA("Vai para o próximo estado quando o paciente envia uma nota"),
    PERMITE_CONSIDERACAO_PROF("Permite que o profissional adicione considerações no prontuários"),
    ESTADO_PACIENTE("Interação principal do paciente"),
    EMAIL_PCTE_TROCA_ESTADO("Envia e-mail para o paciente ao entrar neste estado"),
    AVALIACAO_RETORNO_SUSPEITO("Estado de avaliação de um suspeito"),
    INICIO_CLINICA("Estado inicial de um atendimento clínico"),
    INICIO_SAUDE_MENTAL("Estado inicial de um atendimento da saúde mental");
    
    
    
    
    private final String descricao;

    private IndicadorEstado(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
