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

import java.util.List;
import java.util.TimeZone;
import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class JavaTelemedApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(JavaTelemedApplication.class, args);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver = new PageableHandlerMethodArgumentResolver();
        pageableHandlerMethodArgumentResolver.setFallbackPageable(PageRequest.of(0, 20));
        resolvers.add(pageableHandlerMethodArgumentResolver);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
    }

    /*
    @Bean
    public CommandLineRunner demoTipoProficional(TipoProfissionalService tipoProfissionalService) {
        return (args) -> {
            final Iterable<TipoProfissional> tipos = tipoProfissionalService.findAll();
            tipos.forEach(tipo -> System.out.println(tipo.getDescricao() + " - " + tipo.getSiglaConselho()));
        };
    }

    @Bean
    public CommandLineRunner demoEstado(EstadoService estadoService) {
        return (args) -> {
            final Iterable<Estado> estados = estadoService.findAll();
            estados.forEach(estado -> System.out.println(estado.getSigla() + " - " + estado.getNome()));
        };
    }

    @Bean
    public CommandLineRunner demoCidade(EstadoService estadoService, CidadeService cidadeService) {
        return (args) -> {
            final Estado rs = estadoService.findBySigla("RS");
            final Iterable<Cidade> cidades = cidadeService.findByEstado(rs);
            cidades.forEach(cidade -> System.out.println(cidade.getCodigo() + " - " + cidade.getNome() + " - " + cidade.getEstado().getSigla()));
        };
    }

    @Bean
    public CommandLineRunner demoProfissional(TipoProfissionalService tipoProfissionalService, ProfissionalService profissionalService) {
        return (args) -> {
            final Optional<TipoProfissional> tipo = tipoProfissionalService.findById(3L);
            final Profissional medico = new Profissional();
            medico.setCpf("123.456.789-00");
            medico.setNome("Jack");
            medico.setSobrenome("Bauer");
            medico.setEmail("teste@ufsm.br");
            medico.setSenha("teste001");
            medico.setDataNascimento(new Date());
            medico.setSexo("M");
            medico.setTipoProfissional(tipo.orElse(null));
            medico.setNumeroRegistro("123456");
            profissionalService.save(medico);
            final Optional<Profissional> profissional = profissionalService.findByCpf("123.456.789-00");
            System.out.println(profissional.map(p -> p.getNome() + " " + p.getSobrenome()).orElse(null));
            profissionalService.delete(medico);
        };
    }*/
}
