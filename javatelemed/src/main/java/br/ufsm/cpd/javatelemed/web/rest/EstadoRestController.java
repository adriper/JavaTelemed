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
package br.ufsm.cpd.javatelemed.web.rest;

import br.ufsm.cpd.javatelemed.persistence.model.Cidade;
import br.ufsm.cpd.javatelemed.persistence.model.Estado;
import br.ufsm.cpd.javatelemed.persistence.service.CidadeService;
import br.ufsm.cpd.javatelemed.persistence.service.EstadoService;
import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@RestController
public class EstadoRestController {

    @Autowired
    private CidadeService cidadeService;
    @Autowired
    private EstadoService estadoService;

    @GetMapping("/atendimento/cidades")
    public Collection<Cidade> cidadesByEstado(@RequestParam(value = "idEstado") Long idEstado) {
        final Optional<Estado> estado = estadoService.findById(idEstado);
        return cidadeService.findByEstado(estado.get());
    }

    @GetMapping("/estado/cidades")
    public Collection<Cidade> cidadesEstado(@RequestParam(value = "idEstado") Long idEstado) {
        final Optional<Estado> estado = estadoService.findById(idEstado);
        return cidadeService.findByEstado(estado.get());
    }

    @GetMapping({"/atendimento/cidadesSede", "/estado/cidadesSede"})
    public Collection<Cidade> cidadesSede(@RequestParam(value = "idEstado") Long idEstado) {
        return cidadeService.findCidadesSede(idEstado);
    }
}
