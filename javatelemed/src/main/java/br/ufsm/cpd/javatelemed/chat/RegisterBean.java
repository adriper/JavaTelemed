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
package br.ufsm.cpd.javatelemed.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.util.HtmlUtils;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
@Controller
public class RegisterBean {

    @Autowired
    private GenericWebApplicationContext context;

    @GetMapping("/public/criasala/{nome}")
    protected void criaSala(@PathVariable("nome") final String nomeSala) {
        context.registerBean(ChatController.class, () -> new ChatController());
    }

//    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
//    @MessageMapping("/hello")
//    @SendTo("{")
//    public Greeting greeting(HelloMessage message) throws Exception {
//        return new Greeting(HtmlUtils.htmlEscape(message.getName()) + "!");
//    }

}
