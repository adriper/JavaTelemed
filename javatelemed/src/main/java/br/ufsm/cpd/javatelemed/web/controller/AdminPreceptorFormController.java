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
package br.ufsm.cpd.javatelemed.web.controller;

import br.ufsm.cpd.javatelemed.persistence.model.Cidade;
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Controller
@RequestMapping("/admin/profissional/preceptor/{id}")
public class AdminPreceptorFormController extends PreceptorFormController {

    @Override
    protected String getFormView() {
        return "profissional/preceptor/form";
    }

    @Override
    protected String getSuccessView() {
        return getFormView();
    }

    @Override
    protected Profissional getProfissional(Long idProfissional, Authentication authentication) {
        return profissionalService.findById(idProfissional).orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

    }

    @Override
    protected String getUrlInativa() {
        return "/admin/profissional/preceptor/inativa/";
    }

    @Override
    protected String getUrlForm(ProfissionalPreceptorCommand command) {
        return "/admin/profissional/preceptor/" + command.getPreceptor().getId();
    }

}
