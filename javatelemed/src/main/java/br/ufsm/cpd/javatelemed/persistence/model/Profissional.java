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

import br.ufsm.cpd.javatelemed.commons.collection.CollectionUtils;
import br.ufsm.cpd.javatelemed.commons.persistence.Entidade;
import static br.ufsm.cpd.javatelemed.persistence.model.UserAuthority.ROLE_ADMIN;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.WhereJoinTable;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
@Entity
@DynamicUpdate
public class Profissional extends Entidade implements UserDetails {

    private static final long serialVersionUID = -133318700670800645L;
    @Id
    @Column(name = "id_profissional")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ind_ativo")
    private Boolean ativo;
    @Column(name = "ind_senha_ativa")
    private Boolean senhaAtiva;
    @CPF
    private String cpf;
    @NotBlank
    private String nome;
    @NotBlank
    private String sobrenome;
    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_nascimento")
    private Date dataNascimento;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Sexo sexo;
    private String numeroRegistro;
    @Email
    @NotBlank
    private String email;
    private String telefone;
//    @NotBlank
    private String senha;
    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "id_tipo_profissional")
    private TipoProfissional tipoProfissional;
    @NotNull
    @Column(name = "papel", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserAuthority papel;
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "profissional_especialidade",
            joinColumns = @JoinColumn(name = "id_profissional"),
            inverseJoinColumns = @JoinColumn(name = "id_especialidade"))
    private Set<Especialidade> especialidades;
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "profissional_cidade_sede",
            joinColumns = @JoinColumn(name = "id_profissional"),
            inverseJoinColumns = @JoinColumn(name = "id_cidade"))
    private Set<Cidade> cidadesSede;
    @JsonIgnore
    @JoinTable(
            name = "profissional_preceptor",
            joinColumns = @JoinColumn(name = "id_aluno"),
            inverseJoinColumns = @JoinColumn(name = "id_preceptor")
    )
    @WhereJoinTable(clause = "validade > current_timestamp and inicio <= current_timestamp and ativo is true")
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Profissional> preceptoresResponsaveis;
    @JsonIgnore
    @OneToMany(mappedBy = "preceptor", fetch = FetchType.LAZY)
    private Set<ProfissionalPreceptor> alunos;

    public Profissional() {
        ativo = false;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(final String cpf) {
        this.cpf = cpf;
    }

    public String getNomeSobrenome() {
        return getNomeCompleto();
    }

    public String getNomeSobrenomePrefix() {
        final String prefix = tipoProfissional.getPodeSerPreceptor() ? (sexo == Sexo.F ? "Dra. " : "Dr. ") : "";
        return prefix + getNomeCompleto();
    }

    public String getNomeSobrenomePrefixNumeroRegistro() {
        String retorno = getNomeSobrenomePrefix();
        if (StringUtils.isNotBlank(numeroRegistro)) {
            retorno += " (";
            retorno += tipoProfissional.getSiglaConselho();
            retorno += " " + numeroRegistro.trim() + ")";
        }
        return retorno;
    }

    public boolean isEstudante() {
        return !getTipoProfissional().podeSerPreceptor();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(final String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(final String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(final Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(final Sexo sexo) {
        this.sexo = sexo;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(final String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(final String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(final String senha) {
        this.senha = senha;
    }

    public TipoProfissional getTipoProfissional() {
        return tipoProfissional;
    }

    public void setTipoProfissional(final TipoProfissional tipoProfissional) {
        this.tipoProfissional = tipoProfissional;
    }

    public UserAuthority getPapel() {
        return papel;
    }

    public void setPapel(final UserAuthority papel) {
        this.papel = papel;
    }

    public Set<Especialidade> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(Set<Especialidade> especialidades) {
        this.especialidades = especialidades;
    }

    public String getNomeCompleto() {
        return this.nome + " " + this.sobrenome;
    }

    public Set<Cidade> getCidadesSede() {
        return cidadesSede;
    }

    public void setCidadesSede(Set<Cidade> cidadesSede) {
        this.cidadesSede = cidadesSede;
    }

    public Set<Profissional> getPreceptoresResponsaveis() {
        return preceptoresResponsaveis;
    }

    public void setPreceptoresResponsaveis(Set<Profissional> preceptoresResponsaveis) {
        this.preceptoresResponsaveis = preceptoresResponsaveis;
    }

    public Profissional getPreceptorResponsavel() {
        if (CollectionUtils.isEmpty(preceptoresResponsaveis)) {
            return null;
        } else {
            return preceptoresResponsaveis.iterator().next();
        }
    }

    public Set<ProfissionalPreceptor> getAlunos() {
        return alunos;
    }

    public void setAlunos(Set<ProfissionalPreceptor> alunos) {
        this.alunos = alunos;
    }

    public Boolean getSenhaAtiva() {
        return senhaAtiva;
    }

    public void setSenhaAtiva(Boolean senhaAtiva) {
        this.senhaAtiva = senhaAtiva;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.cpf);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Profissional other = (Profissional) obj;
        return Objects.equals(this.cpf, other.cpf);
    }

    // implementação de UserDetails do SpringSecurity
    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public String getPassword() {
        return getSenha();
    }

    @Override
    public boolean isAccountNonExpired() {
        return getSenhaAtiva();
    }

    @Override
    public boolean isAccountNonLocked() {
        return getAtivo();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return getSenhaAtiva();
    }

    @Override
    public boolean isEnabled() {
        return getAtivo();
    }

    public Boolean podeSerPreceptor() {
        return tipoProfissional.podeSerPreceptor();
    }

    public Boolean getPodeSerPreceptor() {
        return podeSerPreceptor();
    }

    public Boolean podeSerResponsavelFicha() {
        return podeSerPreceptor();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Optional.ofNullable(papel)
                .map(p -> p.getAuthorities())
                .orElse(Collections.EMPTY_SET);
    }

    @JsonIgnore
    public boolean isAdmin() {
        return UserAuthority.ROLE_ADMIN.equals(papel);
    }

    @JsonIgnore
    public boolean isGestorProfissional() {
        return UserAuthority.ROLE_GESTOR_PROFISSIONAL.equals(papel);
    }

    public Profissional getResponsavel() {
        return this.getPreceptorResponsavel() != null ? this.getPreceptorResponsavel() : this;
    }

    @JsonIgnore
    public UserAuthority[] getPossiveisPapeisCadastroProfissional() {
        switch (papel) {
            case ROLE_ADMIN:
                return new UserAuthority[]{UserAuthority.ROLE_ADMIN, UserAuthority.ROLE_GESTOR_PROFISSIONAL, UserAuthority.ROLE_USER};
            case ROLE_GESTOR_PROFISSIONAL:
                return new UserAuthority[]{UserAuthority.ROLE_USER};
        }
        return null;
    }

}
