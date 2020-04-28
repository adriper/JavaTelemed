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
package br.ufsm.cpd.javatelemed.atendimento.rest;

import java.io.Serializable;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
public class Url implements Serializable{
    private String url;

    public Url(String url) {
        this.url = url;
    }

    public String getUrl() {
        return "<a href=\""+url+"\" target=\"blank\">" + url + "</a>";
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    
}
