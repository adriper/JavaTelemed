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
import br.ufsm.cpd.javatelemed.utils.CriptoConverter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import org.springframework.web.util.HtmlUtils;
import org.thymeleaf.util.DateUtils;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Entity
public class NotaAtendimento extends Entidade implements Comparable<NotaAtendimento> {

    @Id
    @Column(name = "id_nota")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nota_sequence")
    @SequenceGenerator(name = "nota_sequence", sequenceName = "nota_atendimento_id_nota_seq", allocationSize = 1)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id_ficha")
    @JsonIgnore
    private FichaAtendimento fichaAtendimento;
    @ManyToOne
    @JoinColumn(name = "id_profissional")
    @JsonIgnore
    private Profissional profissional;
    @ManyToOne
    @JoinColumn(name = "id_estudante")
    @JsonIgnore
    private Profissional estudante;
    @Convert(converter = CriptoConverter.class)
    @NotEmpty(message = "Por favor, preencha a nota da ficha de atendimento")
    private String descricao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date momentoCriacao;
    @ManyToOne
    @JoinColumn(name = "id_estado_consulta")
    @JsonIgnore
    private EstadoConsulta estadoConsulta;
//    @JsonIgnore
    @Column(name = "file_name")
    private String fileName;
    @JsonIgnore
    @Column(name = "file_type")
    private String fileType;
    @JsonIgnore
    @Column(name = "file_data")
    private byte[] fileData;
    @Temporal(TemporalType.TIMESTAMP)
    private Date momentoAcessoPaciente;
    @Transient
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public NotaAtendimento() {
        momentoCriacao = DateUtils.createNow().getTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FichaAtendimento getFichaAtendimento() {
        return fichaAtendimento;
    }

    public void setFichaAtendimento(FichaAtendimento fichaAtendimento) {
        this.fichaAtendimento = fichaAtendimento;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }

    public String getDescricao() {
        if (getIsPaciente()) {
            return HtmlUtils.htmlEscape(descricao);
        }
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getMomentoCriacao() {
        return momentoCriacao;
    }

    public void setMomentoCriacao(Date momentoCriacao) {
        this.momentoCriacao = momentoCriacao;
    }

    @Override
    public int compareTo(NotaAtendimento o) {
        return momentoCriacao.compareTo(o.getMomentoCriacao());
    }

    public Profissional getEstudante() {
        return estudante;
    }

    public void setEstudante(Profissional estudante) {
        this.estudante = estudante;
    }

    public EstadoConsulta getEstadoConsulta() {
        return estadoConsulta;
    }

    public void setEstadoConsulta(EstadoConsulta estadoConsulta) {
        this.estadoConsulta = estadoConsulta;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.id);
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
        final NotaAtendimento other = (NotaAtendimento) obj;
        if (!Objects.equals(this.descricao, other.descricao)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.fichaAtendimento.getId(), other.fichaAtendimento.getId())) {
            return false;
        }
        if (!Objects.equals(this.profissional, other.profissional)) {
            return false;
        }
        if (!Objects.equals(this.momentoCriacao, other.momentoCriacao)) {
            return false;
        }
        return true;
    }

    @JsonGetter
    public String getNomeResponsavel() {
        if (profissional != null) {
            return profissional.getNomeCompleto();
        }
        return null;
    }

    @JsonGetter
    public String getNomeResponsavelPrefix() {
        if (profissional != null) {
            return profissional.getNomeSobrenomePrefix();
        }
        return null;
    }

    @JsonGetter
    public String getNomeAutor() {
        if (profissional != null) {
            if (estudante != null) {
                return estudante.getNomeCompleto() + " - supervisionad" + (estudante.getSexo().equals(Sexo.F) ? "a " : "o ") + " por " + profissional.getNomeSobrenomePrefix();
            } else {
                return profissional.getNomeSobrenomePrefix();
            }
        } else {
            return fichaAtendimento.getNome();
        }
    }

    @JsonGetter
    public boolean getIsPaciente() {
        return profissional == null;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    @JsonGetter
    public Boolean getPossuiArquivo() {
        return fileData != null;
    }

    public Date getMomentoAcessoPaciente() {
        return momentoAcessoPaciente;
    }

    public void setMomentoAcessoPaciente(Date momentoAcessoPaciente) {
        this.momentoAcessoPaciente = momentoAcessoPaciente;
    }

    @JsonGetter
    public String getMomentoAcessoPacienteFormatado() {
        if (momentoAcessoPaciente != null) {
            return simpleDateFormat.format(momentoAcessoPaciente);
        }
        return null;
    }
}
