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

import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import static br.ufsm.cpd.javatelemed.persistence.model.UserAuthority.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    public static final String INDEX_PAGE = "/home";
    public static final String DEFAULT_SUCCESS_LOGIN = "/user/atendimento/lista/";

    @Autowired
    private ProfissionalService profissionalService;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // publicos
                .authorizeRequests().antMatchers("/", "/home", "/login", "/logout", "/public/**", "/gs-guide-websocket/**", "/atendimento/**", "/visualizar").permitAll()
                .and()
                // requer admin
                .authorizeRequests().antMatchers("/admin/profissional/**").hasAnyAuthority(ROLE_ADMIN.name(), ROLE_GESTOR_PROFISSIONAL.name())
                .antMatchers("/admin/**").hasAuthority(ROLE_ADMIN.name())
                .and()
                // demais requerem alguma autenticação (user ou admin)
                .authorizeRequests().anyRequest().authenticated()
                .and()
                // config o login
                .formLogin().loginPage("/login").defaultSuccessUrl(DEFAULT_SUCCESS_LOGIN).permitAll()
// fiz um logout manual pra funcionar com GET independente do csrf token... :/
//                .and()
//                // config o logout
//                .logout().logoutUrl("/logout")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
//                .logoutSuccessUrl("/login?logout").permitAll()
                ;
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(profissionalService).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring().antMatchers("/assets/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
//        PasswordEncoder encoder = new MessageDigestPasswordEncoder("SHA-256");
//        PasswordEncoder encoder = new MessageDigestPasswordEncoder("SHA-1");
//        PasswordEncoder encoder = new MessageDigestPasswordEncoder("MD5");
        // o spring recomenda esse. os de cima estão deprecados, mas se tiver que usar...
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }

}
