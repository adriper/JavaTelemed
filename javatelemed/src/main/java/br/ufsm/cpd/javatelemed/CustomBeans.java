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

import br.ufsm.cpd.javatelemed.password.rule.DigitRule;
import br.ufsm.cpd.javatelemed.password.ConfigurablePasswordChecker;
import br.ufsm.cpd.javatelemed.password.PasswordChecker;
import br.ufsm.cpd.javatelemed.password.rule.BlacklistRule;
import br.ufsm.cpd.javatelemed.password.rule.CharTypeDiversityRule;
import br.ufsm.cpd.javatelemed.password.rule.DadosPessoaisRule;
import br.ufsm.cpd.javatelemed.password.rule.LengthRule;
import br.ufsm.cpd.javatelemed.password.rule.LowercaseRule;
import br.ufsm.cpd.javatelemed.password.rule.PreviousPasswordRule;
import br.ufsm.cpd.javatelemed.password.rule.SymbolRule;
import br.ufsm.cpd.javatelemed.password.rule.UppercaseRule;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Properties;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
@Configuration
public class CustomBeans {

    public static final Locale PT_BR_LOCALE = new Locale("pt", "BR");
    public static final String DECIMAL_PATTERN = "#,###.##";
    public static final String MONEY_PATTERN = "#,###.00";

    @Bean(name = "dateFormat")
    public DateFormat dateFormat() {
        final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        return dateFormat;
    }

    @Bean(name = "dateTimeFormat")
    public DateFormat dateTimeFormat() {
        final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        dateFormat.setLenient(false);
        return dateFormat;
    }

    @Bean(name = "timeFormat")
    public DateFormat timeFormat() {
        final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setLenient(false);
        return dateFormat;
    }

    @Bean(name = "locale")
    public Locale locale() {
        return PT_BR_LOCALE;
    }

    @Autowired
    @Bean(name = "decimalFormatSymbols")
    public DecimalFormatSymbols decimalFormatSymbols(Locale locale) {
        return DecimalFormatSymbols.getInstance(locale);
    }

    @Autowired
    @Bean(name = "moneyFormat")
    public NumberFormat moneyFormat(final DecimalFormatSymbols decimalFormatSymbols) {
        final DecimalFormat numberFormatter = new DecimalFormat(MONEY_PATTERN, decimalFormatSymbols);
        numberFormatter.setParseBigDecimal(true);
        return numberFormatter;
    }

    @Autowired
    @Bean(name = "decimalFormat")
    public NumberFormat decimalFormat(final DecimalFormatSymbols decimalFormatSymbols) {
        final DecimalFormat numberFormatter = new DecimalFormat(DECIMAL_PATTERN, decimalFormatSymbols);
        numberFormatter.setParseBigDecimal(true);
        return numberFormatter;
    }

    @Bean
    public VelocityEngine velocityEngine() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("input.encoding", "UTF-8");
        properties.setProperty("output.encoding", "UTF-8");
        properties.setProperty("resource.loader", "class");
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        return new VelocityEngine(properties);
    }

    @Bean
    @Autowired
    public PasswordChecker passwordChecker(DadosPessoaisRule dadosPessoaisRule, PreviousPasswordRule previousPasswordRule) {
        final ConfigurablePasswordChecker passwordChecker = new ConfigurablePasswordChecker();
        passwordChecker.addRule(new BlacklistRule());
        passwordChecker.addRule(new LowercaseRule());
        passwordChecker.addRule(new UppercaseRule());
        passwordChecker.addRule(new DigitRule());
        passwordChecker.addRule(new SymbolRule());
        passwordChecker.addRule(new LengthRule(8, 128));
        passwordChecker.addRule(dadosPessoaisRule);
        passwordChecker.addRule(previousPasswordRule);

        // somente score
        final CharTypeDiversityRule charTypeDiversityRule = new CharTypeDiversityRule();
        charTypeDiversityRule.setValidationActive(false);
        passwordChecker.addRule(charTypeDiversityRule);
        return passwordChecker;
    }
}
