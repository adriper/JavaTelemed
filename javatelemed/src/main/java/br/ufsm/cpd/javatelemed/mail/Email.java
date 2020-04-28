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
package br.ufsm.cpd.javatelemed.mail;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
public class Email implements Serializable {

    private static final String DEFAULT_PATH = "br/ufsm/cpd/javatelemed/mail/";
    private String encoding;
    private String assunto;
    private String destinatario;
    private String templateName;
    private Map<String, Object> parametros;

    public Email() {
        this.encoding = "UTF-8";
        this.parametros = new HashMap<>();
    }

    public Email(String destino, String assunto, String templateName) {
        this();
        this.assunto = assunto;
        this.destinatario = destino;
        this.templateName = templateName;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Map<String, Object> getParametros() {
        return parametros;
    }

    public void setParametros(Map<String, Object> parametros) {
        this.parametros = parametros;
    }

    public void addParametro(String key, Object value) {
        this.parametros.put(key, value);
    }

    public String getTemplatePath() {
        return DEFAULT_PATH + this.getTemplateName() + ".vm";
    }
}
