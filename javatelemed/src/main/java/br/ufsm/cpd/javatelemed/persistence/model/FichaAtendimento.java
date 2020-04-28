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

import br.ufsm.cpd.javatelemed.commons.persistence.Entidade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.core.instrument.util.StringUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.DateUtils;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Entity
public class FichaAtendimento extends Entidade implements Comparable<FichaAtendimento> {

    public static final int MAX_SALA_ATENDIMENTO = 64;

    @Id
    @Column(name = "id_ficha")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ficha_sequence")
    @SequenceGenerator(name = "ficha_sequence", sequenceName = "ficha_atendimento_id_ficha_seq", allocationSize = 1)
    private Long id;
    @JsonIgnore
    private String protocolo;
    @JsonIgnore
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    private String senha;
    @NotNull
//    @NotEmpty
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    private String nome;
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    private String nomeResponsavelLegal;
    @CPF
    @NotNull
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    private String cpf;
    @CPF
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    private String cpfResponsavelLegal;
    @NotNull
//    @NotEmpty
    @Size(max = 255)
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    private String nomeMae;
//    @NotEmpty
    @NotNull
    @Size(max = 255)
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    private String endereco;
//    @NotEmpty
    @NotNull
    @Size(max = 255)
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    @Column(name = "ENDERECO_NUMERO")
    private String numeroEndereco;
//    @NotEmpty
    @NotNull
    @Size(max = 9)
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    private String cep;
//    @NotEmpty
    @NotNull
    @Size(max = 255)
    private String profissao;
//    @NotEmpty
    @NotNull
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    private String telefone;
    @Email
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    private String email;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_cidade")
    private Cidade cidade;
    @ManyToOne
    @JoinColumn(name = "id_cidade_sede")
    private Cidade cidadeSedeAtendimento;
    @ManyToOne
    @JoinColumn(name = "id_estadoConsulta")
    private EstadoConsulta estadoConsulta;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @NotNull
    private Date dataNascimento;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dataNascimentoResponsavelLegal;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Sexo sexo;
    @NotNull
    private String bairro;
    private String convenioSaude;
    private String viagemRecente;
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    private String contatoCasoConfirmado;
    /*Sintomas*/
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dataInicialSintomas;
    @Column(name = "ind_febre")
    private Boolean febre;
    @Column(name = "ind_cefaleia")
    private Boolean cefaleia;
    @Column(name = "ind_mialgia")
    private Boolean mialgia;
    @Column(name = "ind_tosse")
    private Boolean tosse;
    @Column(name = "ind_dispneia")
    private Boolean dispneia;
    @Size(max = 255)
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    private String outrosSintomas;
    /*Doenças crônicas*/
    @Column(name = "ind_diabetes")
    private Boolean diabetes;
    @Column(name = "ind_hipertensao")
    private Boolean hipertensao;
    @Column(name = "ind_cardiopatia")
    private Boolean cardiopatia;
    @Column(name = "ind_asma")
    private Boolean asma;
    @Size(max = 255)
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    private String outrasDoencas;
    @Size(max = 255)
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    private String alergiaMedicamentos;
    @Size(max = 255)
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    private String medicacaoContinua;
    @Column(name = "ind_prioridade")
    private String prioridade;
    private String ip;
    @Temporal(TemporalType.TIMESTAMP)
    private Date momentoCriacao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date momentoUltimaMensagem;
    @Column(name = "ind_suspeito")
    @NotNull
    @Enumerated(EnumType.STRING)
    private SituacaoSuspeito situacaoSuspeito;
    @Column(name = "descricao")
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    private String descricao;
    @OneToMany(mappedBy = "fichaAtendimento")
    @JsonIgnore
    @OrderBy(value = "momentoCriacao desc")
    private Set<NotaAtendimento> notas;
    @ManyToOne
    @JoinColumn(name = "id_profissional")
    private Profissional responsavel;
    @ManyToOne
    @JoinColumn(name = "id_estudante")
    private Profissional estudante;
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    private String linkReceita;
    @Convert(converter = br.ufsm.cpd.javatelemed.utils.CriptoConverter.class)
    private String consideracoesProfissional;
    @JsonIgnore
    @NotNull
    private Boolean declaracaoVeracidade;
    @JsonIgnore
    private String termoAceite;
    @JsonIgnore
    @Size(max = MAX_SALA_ATENDIMENTO)
    private String salaAtendimento;

    public FichaAtendimento() {
        momentoCriacao = DateUtils.createNow().getTime();
        situacaoSuspeito = SituacaoSuspeito.N;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public Cidade getCidadeSedeAtendimento() {
        return cidadeSedeAtendimento;
    }

    public void setCidadeSedeAtendimento(Cidade cidadeSedeAtendimento) {
        this.cidadeSedeAtendimento = cidadeSedeAtendimento;
    }

    public EstadoConsulta getEstadoConsulta() {
        return estadoConsulta;
    }

    public void setEstadoConsulta(EstadoConsulta estadoConsulta) {
        this.estadoConsulta = estadoConsulta;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getConvenioSaude() {
        return convenioSaude;
    }

    public void setConvenioSaude(String convenioSaude) {
        this.convenioSaude = convenioSaude;
    }

    public String getViagemRecente() {
        return viagemRecente;
    }

    public void setViagemRecente(String viagemRecente) {
        this.viagemRecente = viagemRecente;
    }

    public String getContatoCasoConfirmado() {
        return contatoCasoConfirmado;
    }

    public void setContatoCasoConfirmado(String contatoCasoConfirmado) {
        this.contatoCasoConfirmado = contatoCasoConfirmado;
    }

    public Date getDataInicialSintomas() {
        return dataInicialSintomas;
    }

    public void setDataInicialSintomas(Date dataInicialSintomas) {
        this.dataInicialSintomas = dataInicialSintomas;
    }

    public Boolean getFebre() {
        return febre;
    }

    public void setFebre(Boolean febre) {
        this.febre = febre;
    }

    public Boolean getCefaleia() {
        return cefaleia;
    }

    public void setCefaleia(Boolean cefaleia) {
        this.cefaleia = cefaleia;
    }

    public Boolean getMialgia() {
        return mialgia;
    }

    public void setMialgia(Boolean mialgia) {
        this.mialgia = mialgia;
    }

    public Boolean getTosse() {
        return tosse;
    }

    public void setTosse(Boolean tosse) {
        this.tosse = tosse;
    }

    public Boolean getDispneia() {
        return dispneia;
    }

    public void setDispneia(Boolean dispneia) {
        this.dispneia = dispneia;
    }

    public String getOutrosSintomas() {
        return outrosSintomas;
    }

    public void setOutrosSintomas(String outrosSintomas) {
        this.outrosSintomas = outrosSintomas;
    }

    public Boolean getDiabetes() {
        return diabetes;
    }

    public void setDiabetes(Boolean diabetes) {
        this.diabetes = diabetes;
    }

    public Boolean getHipertensao() {
        return hipertensao;
    }

    public void setHipertensao(Boolean hipertensao) {
        this.hipertensao = hipertensao;
    }

    public Boolean getCardiopatia() {
        return cardiopatia;
    }

    public void setCardiopatia(Boolean cardiopatia) {
        this.cardiopatia = cardiopatia;
    }

    public Boolean getAsma() {
        return asma;
    }

    public void setAsma(Boolean asma) {
        this.asma = asma;
    }

    public String getOutrasDoencas() {
        return outrasDoencas;
    }

    public void setOutrasDoencas(String outrasDoencas) {
        this.outrasDoencas = outrasDoencas;
    }

    public String getMedicacaoContinua() {
        return medicacaoContinua;
    }

    public void setMedicacaoContinua(String medicacaoContinua) {
        this.medicacaoContinua = medicacaoContinua;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getMomentoCriacao() {
        return momentoCriacao;
    }

    public void setMomentoCriacao(Date momentoCriacao) {
        this.momentoCriacao = momentoCriacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getMomentoUltimaMensagem() {
        return momentoUltimaMensagem;
    }

    public void setMomentoUltimaMensagem(Date momentoUltimaMensagem) {
        this.momentoUltimaMensagem = momentoUltimaMensagem;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getEnderecoCompleto() {
        return getEndereco() + (StringUtils.isNotBlank(getNumeroEndereco()) ? " - " + getNumeroEndereco() : "");
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumeroEndereco() {
        return numeroEndereco;
    }

    public void setNumeroEndereco(String numeroEndereco) {
        this.numeroEndereco = numeroEndereco;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public SituacaoSuspeito getSituacaoSuspeito() {
        return situacaoSuspeito;
    }

    public boolean isSuspeito() {
        return !situacaoSuspeito.equals(SituacaoSuspeito.N);
    }

    public boolean isSuspeitoGrave() {
        return situacaoSuspeito.isSuspeitoGrave();
    }

    public boolean isSuspeitoLeve() {
        return situacaoSuspeito.isSuspeitoLeve();
    }

    public boolean isSuspeitoLeveSemRisco() {
        return situacaoSuspeito.isSuspeitoLeveSemRisco();
    }

    public boolean isSuspeitoLeveComRisco() {
        return situacaoSuspeito.isSuspeitoLeveComRisco();
    }

    public void setSituacaoSuspeito(SituacaoSuspeito suspeito) {
        this.situacaoSuspeito = suspeito;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public Set<NotaAtendimento> getNotas() {
        return notas;
    }

    public String getNomeResponsavelLegal() {
        return nomeResponsavelLegal;
    }

    public void setNomeResponsavelLegal(String nomeResponsavelLegal) {
        this.nomeResponsavelLegal = nomeResponsavelLegal;
    }

    public String getCpfResponsavelLegal() {
        return cpfResponsavelLegal;
    }

    public void setCpfResponsavelLegal(String cpfResponsavelLegal) {
        this.cpfResponsavelLegal = cpfResponsavelLegal;
    }

    public Date getDataNascimentoResponsavelLegal() {
        return dataNascimentoResponsavelLegal;
    }

    public void setDataNascimentoResponsavelLegal(Date dataNascimentoResponsavelLegal) {
        this.dataNascimentoResponsavelLegal = dataNascimentoResponsavelLegal;
    }

    public Set<NotaAtendimento> getNotas(final Profissional profissional) {
        if (CollectionUtils.isEmpty(notas) || profissional.isAdmin()) {
            return notas;
        }
        final Set<NotaAtendimento> notasToReturn = new LinkedHashSet<>();
        final Set<EstadoConsulta> estadosConsulta = profissional.getTipoProfissional().getEstadosConsulta();
        for (final NotaAtendimento nota : notas) {
            if (estadosConsulta.contains(nota.getEstadoConsulta())) {
                notasToReturn.add(nota);
            }
        }
        return notasToReturn;

    }

    public void setNotas(Set<NotaAtendimento> notas) {
        this.notas = notas;
    }

    public Profissional getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Profissional responsavel) {
        this.responsavel = responsavel;
    }

    public Integer getIdade() {
        return br.ufsm.cpd.javatelemed.utils.DateUtils.getDiffYears(dataNascimento, new Date());
    }

    public Integer getIdadeResponsavelLegal() {
        return br.ufsm.cpd.javatelemed.utils.DateUtils.getDiffYears(dataNascimentoResponsavelLegal, new Date());
    }

    public String getAlergiaMedicamentos() {
        return alergiaMedicamentos;
    }

    public void setAlergiaMedicamentos(String alergiaMedicamentos) {
        this.alergiaMedicamentos = alergiaMedicamentos;
    }

    public String getLinkReceita() {
        return linkReceita;
    }

    public void setLinkReceita(String linkReceita) {
        this.linkReceita = linkReceita;
    }

    public Profissional getEstudante() {
        return estudante;
    }

    public void setEstudante(Profissional estudante) {
        this.estudante = estudante;
    }

    public String getConsideracoesProfissional() {
        return consideracoesProfissional;
    }

    public void setConsideracoesProfissional(String consideracoesProfissional) {
        this.consideracoesProfissional = consideracoesProfissional;
    }

    public Boolean getDeclaracaoVeracidade() {
        return declaracaoVeracidade;
    }

    public void setDeclaracaoVeracidade(Boolean declaracaoVeracidade) {
        this.declaracaoVeracidade = declaracaoVeracidade;
    }

    public String getNomeResponsavelComEstudante() {
        if (estudante != null) {
            return estudante.getNomeSobrenomePrefix() + " - supervisionad"
                    + (estudante.getSexo().equals(Sexo.F) ? "a" : "o")
                    + " por " + responsavel.getNomeSobrenomePrefixNumeroRegistro();
        }
        return responsavel.getNomeSobrenomePrefixNumeroRegistro();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.id);
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
        final FichaAtendimento other = (FichaAtendimento) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(FichaAtendimento o) {
        return getMomentoCriacao().compareTo(o.getMomentoCriacao());
    }

//    public Boolean podeAlterarFicha(final Profissional profissional){
//        if (!profissional.equals(getResponsavel()) && !profissional.equals(getEstudante())){
//            return false;            
//        }
//       return !estadoConsulta.getAtendimentoFinalizado();
//    }
    public void addNota(NotaAtendimento nota) {
        if (notas == null) {
            notas = new TreeSet<NotaAtendimento>();
        }
        nota.setFichaAtendimento(this);
        notas.add(nota);
        momentoUltimaMensagem = nota.getMomentoCriacao();
    }

    public boolean getFinalizado() {
        return estadoConsulta.getAtendimentoFinalizado();
    }

    public String getTermoAceite() {
        return termoAceite;
    }

    public void setTermoAceite(String termoAceite) {
        this.termoAceite = termoAceite;
    }

    public String getSalaAtendimento() {
        return salaAtendimento;
    }

    public void setSalaAtendimento(String salaAtendimento) {
        this.salaAtendimento = salaAtendimento;
    }

    public enum SituacaoSuspeito {
        N("Não é suspeito"),
        L("Leve sem risco", 48),
        R("Leve com risco", 24),
        G("Grave");
        private final String descricao;
        private final Integer horasRetorno;

        private SituacaoSuspeito(String descricao) {
            this.descricao = descricao;
            horasRetorno = 0;
        }

        private SituacaoSuspeito(String descricao, Integer horasRetorno) {
            this.descricao = descricao;
            this.horasRetorno = horasRetorno;
        }

        public String getDescricao() {
            return descricao;
        }

        public boolean isSuspeitoLeve() {
            return isSuspeitoLeveComRisco() || isSuspeitoLeveSemRisco();
        }

        public boolean isSuspeitoLeveSemRisco() {
            return this.equals(L);
        }

        public boolean isSuspeitoLeveComRisco() {
            return this.equals(R);
        }

        public boolean isSuspeitoGrave() {
            return this.equals(G);
        }

        public boolean isSuspeito() {
            return !this.equals(N);
        }

        public Integer getHorasRetorno() {
            return horasRetorno;
        }

        public static Collection<SituacaoSuspeito> getSituacoesSuspeito(final Boolean onlySuspeitos) {
            final Collection<SituacaoSuspeito> situacoes = new ArrayList<SituacaoSuspeito>();
            situacoes.addAll(Arrays.asList(values()));
            if (onlySuspeitos) {
                situacoes.remove(SituacaoSuspeito.N);
            }
            return situacoes;
        }
    }

    public void processaConsideracoesProfissional(final String consideracoes, final Profissional profissional) {
        final StringBuilder builder = new StringBuilder();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        final String format = simpleDateFormat.format(DateUtils.createNow().getTime());
        builder.append(profissional.getNomeSobrenomePrefixNumeroRegistro()).append(" em ").append(format);
        builder.append(" escreveu: ").append(consideracoes);
        builder.append("<br />");
        if (this.consideracoesProfissional == null) {
            this.consideracoesProfissional = builder.toString();
        } else {
            this.consideracoesProfissional += builder.toString();
        }
    }
}
