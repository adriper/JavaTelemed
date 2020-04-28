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
package br.ufsm.cpd.javatelemed.utils;

import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
public class TrampaUtils {

    public static final String getHost(final HttpServletRequest request) {
        // trampa pra pegar o host
        final StringBuffer url = request.getRequestURL();
        url.delete(url.indexOf(request.getRequestURI()), url.length());
        return url.toString();
    }

    public static final String getIp(final HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
        }
    }
}
