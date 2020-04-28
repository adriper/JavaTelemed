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

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author mfonseca
 */
public enum UserAuthority {
    ROLE_USER("Usuário"),
    ROLE_ADMIN("Administrador"),
    ROLE_GESTOR_PROFISSIONAL("Gestor de profissionais");
    
    private final String descricao;
    private final Set<UserAuthority> subAuthorities = new TreeSet<>();

    private UserAuthority(final String descricao, final UserAuthority... subSet) {
        this.descricao = descricao;
        this.subAuthorities.addAll(Arrays.asList(subSet));
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isRole() {
        return this.name().startsWith("ROLE_");
    }

    public boolean isGroup() {
        return this.name().startsWith("GROUP_");
    }

    public boolean isPrivilege() {
        return !(isRole() || isGroup());
    }

    public String roleName() {
        return isRole() ? name().substring(5) : name();
    }

    public Set<GrantedAuthority> getAuthorities() {
        return collectAuthorities(this, new TreeSet<>());
    }

    public static String[] names(final UserAuthority... authorities) {
        return Stream.of(authorities.length > 0 ? authorities : values())
                .map(UserAuthority::name)
                .toArray(String[]::new);
    }

    public static String[] roleNames(final UserAuthority... authorities) {
        return Stream.of(authorities.length > 0 ? authorities : values())
                .map(UserAuthority::roleName)
                .toArray(String[]::new);
    }

    public static UserAuthority[] roles(final UserAuthority... authorities) {
        return Stream.of(authorities.length > 0 ? authorities : values())
                .filter(ua -> ua.isRole())
                .toArray(UserAuthority[]::new);
    }

    private static Set<GrantedAuthority> collectAuthorities(UserAuthority authority, Set<GrantedAuthority> effective) {
        final UserGrantedAuthority sga = new UserGrantedAuthority(authority.name());
        if (!effective.contains(sga)) {
            effective.add(sga);
            authority.subAuthorities.forEach((subAuthority) -> {
                collectAuthorities(subAuthority, effective);
            });
        }
        return effective;
    }
    
    public static class UserGrantedAuthority implements GrantedAuthority, Comparable<GrantedAuthority> {

        protected final String name;

        public UserGrantedAuthority(final String authname) {
            this.name = authname;
        }

        @Override
        public String getAuthority() {
            return name;
        }

        @Override
        public int compareTo(GrantedAuthority o) {
            return this.getAuthority().compareTo(o.getAuthority());
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 31 * hash + Objects.hashCode(this.name);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final UserGrantedAuthority other = (UserGrantedAuthority) obj;
            return Objects.equals(this.name, other.name);
        }

    }
}
