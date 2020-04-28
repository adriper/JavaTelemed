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
package br.ufsm.cpd.javatelemed;

import java.util.Map;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.WebRequest;

/**
 *
 * @author mfonseca
 */
@Component
public class MyCustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        final Map<String, Object> map = super.getErrorAttributes(webRequest, true);
        final String error = get("error", map);
        final String msg = get("message", map);
        if ((StringUtils.isEmpty(msg) || "No message available".equals(msg)) && !StringUtils.isEmpty(error)) {
            map.put("message", error);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private <T> T get(String key, Map<String, Object> map) {
        return (T) map.get(key);
    }
}
