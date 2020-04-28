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
package br.ufsm.cpd.javatelemed.mail;

import java.io.StringWriter;
import javax.mail.internet.MimeMessage;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 *
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private VelocityEngine velocityEngine;
    @Value("${br.ufsm.cpd.javatelemed.email.from}")
    private String mailFrom;

    public void sendEmail(final Email email) throws Exception {
        final StringWriter writer = new StringWriter();
        final VelocityContext context = new VelocityContext(email.getParametros());
        velocityEngine.mergeTemplate(email.getTemplatePath(), email.getEncoding(), context, writer);

        final MimeMessage msg = mailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setTo(email.getDestinatario());
        helper.setSubject(email.getAssunto());
        helper.setText(writer.toString(), true);
        helper.setFrom(mailFrom);
//        helper.addAttachment("my_photo.png", inputStreamSource);
        mailSender.send(msg);
    }
}
