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

import br.ufsm.cpd.javatelemed.persistence.model.EstadoConsulta;
import br.ufsm.cpd.javatelemed.persistence.model.IndicadorEstado;
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.model.Sexo;
import br.ufsm.cpd.javatelemed.persistence.model.TipoProfissional;
import br.ufsm.cpd.javatelemed.persistence.model.UserAuthority;
import br.ufsm.cpd.javatelemed.persistence.service.EstadoConsultaService;
import br.ufsm.cpd.javatelemed.persistence.service.ProfissionalService;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Bota algumas coisas no banco no start da app, se necessário.
 * <p>
 * Essa classe é só para inicializarmos algum dado padrão durante o boot para
 * teste manual.
 * <p>
 * DEVE SER REMOVIDO DEPOIS QUE O APP ESTIVER PRONTO!
 *
 * @author mfonseca
 */
@Component
public class Setup {

    private static final Logger LOGGER = Logger.getLogger(Setup.class.getName());
    private static final String EMAIL_ADMIN = "@.com";
    private static final String NOME_ADMIN = "";
    private static final String SOBRENOME_ADMIN = "";
    private static final String CPF_ADMIN = "XXX.XXX.XXX-XX";
    private static final String PASS_ADMIN = "";

    @Autowired
    private ProfissionalService profissionalService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EstadoConsultaService estadoConsultaService;

    // Executa logo que a aplicação terminou de bootar.
    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        Profissional p = profissionalService.findByEmail(EMAIL_ADMIN).orElse(null);
        if (p != null) {
            LOGGER.log(Level.INFO, "Admin ja existe: {0}", p);
        } else {
            p = profissionalService.save(createAdmin());
            LOGGER.log(Level.INFO, "Admin inserido: {0}", p);
        }
        if (estadoConsultaService.count() == 0L){
            createEstadoConsultaBasico();
        }
    }

    // cria um usuario admin pra teste, se necessário
    private Profissional createAdmin() {
        Profissional p = new Profissional();
        p.setEmail(EMAIL_ADMIN);
        p.setSenha(passwordEncoder.encode(PASS_ADMIN));
        p.setNome(NOME_ADMIN);
        p.setSobrenome(SOBRENOME_ADMIN);
        p.setCpf(CPF_ADMIN);
        p.setDataNascimento(Calendar.getInstance().getTime());
//        p.setNumeroRegistro("");
        p.setPapel(UserAuthority.ROLE_ADMIN);
        p.setSexo(Sexo.M);
        p.setSenhaAtiva(Boolean.TRUE);
        p.setAtivo(Boolean.TRUE);
        //p.setTelefone("000");
        p.setTipoProfissional(getTipoProfissional());
        return p;
    }

    private TipoProfissional getTipoProfissional() {
        TipoProfissional tipo = new TipoProfissional();
        tipo.setDescricao("Administrador");
        tipo.setPrescricao(Boolean.FALSE);
        tipo.setVideoconferencia(Boolean.FALSE);
        return tipo;
    }

    private void createEstadoConsultaBasico() {
        final EstadoConsulta estadoInicial = new EstadoConsulta();
        final Set<IndicadorEstado> indicadores = new HashSet<IndicadorEstado>(3);
        indicadores.add(IndicadorEstado.ESTADO_INICIAL);
        indicadores.add(IndicadorEstado.INICIO_CLINICA);
        estadoInicial.setDescricao("Novo");
        estadoInicial.setIndicadores(indicadores);
        estadoConsultaService.save(estadoInicial);
    }
}
