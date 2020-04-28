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

import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.Properties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Advice que disponibiliza algumas infos para todas as views.
 * 
 * @author mfonseca
 */
@ControllerAdvice
public class ApplicationControllerAdvice {

    @ModelAttribute("currentUser")
    public Profissional getCurrentUser(Authentication authentication) {
        return Optional.ofNullable(authentication)
                .map(a -> (Profissional) a.getPrincipal())
                .orElse(null);
    }

    @ModelAttribute("appInfo")
    public AppInfo getAppInfo() {
        return AppInfo.load();
    }

    public static class AppInfo {

        private final Properties props;

        private AppInfo(Properties props) {
            this.props = props;
        }

        public String getName() {
            return props.getProperty("application.name");
        }

        public String getVersion() {
            return props.getProperty("application.version");
        }

        public String getTimestamp() {
            return props.getProperty("application.timestamp");
        }

        public static AppInfo load() {
            ClassPathResource resource = new ClassPathResource("/application.properties");
            Properties props = new Properties();
            try ( InputStream is = resource.getInputStream()) {
                props.load(is);
                return new AppInfo(props);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }
}
