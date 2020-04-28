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
package br.ufsm.cpd.javatelemed.password.rule;

import br.ufsm.cpd.javatelemed.commons.text.TextUtils;
import br.ufsm.cpd.javatelemed.password.PasswordScoreRule;
import br.ufsm.cpd.javatelemed.password.PasswordValidationRule;
import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.utils.DateUtils;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 *
 * @author Giuliano Ferreira
 */
@Component
public class DadosPessoaisRule extends AbstractUsuarioRule implements PasswordValidationRule, PasswordScoreRule {

    private static final long serialVersionUID = 2911332076354072856L;

    public DadosPessoaisRule() {
        super();
        setScoreWeigth(5);
    }

    @Override
    public boolean validate(String password, final Map<String, Object> extraParams) {
        if (TextUtils.isBlankOrNull(password)) {
            return false;
        }
        return Stream.of(getTokens(extraParams)).noneMatch(parte -> StringUtils.containsIgnoreCase(password, parte));
    }

    @Override
    public int score(String password, final Map<String, Object> extraParams) {
        final int max = TextUtils.length(password);
        int lesser = max;
        for (final String token : getTokens(extraParams)) {
            final String builder = password.replace(token, "");
            if (builder.length() < lesser) {
                lesser = builder.length();
            }
        }
        int pass = 100 / max;
        int value = (max - lesser) * pass;
        return 100 - value;
    }

    private String[] getTokens(final Map<String, Object> extraParams) {
        String[] tokens = new String[]{};
        final Profissional usuario = getUsuario(extraParams);
        final Date dataNascimento = usuario.getDataNascimento();
        final String[] datas = new String[]{
            DateUtils.format(dataNascimento, "ddMMyy"),
            DateUtils.format(dataNascimento, "ddMMyyyy"),
            DateUtils.format(dataNascimento, "yyyy"),
            DateUtils.format(dataNascimento, "ddMM")
        };
        tokens = ArrayUtils.addAll(tokens, datas);
        tokens = ArrayUtils.addAll(tokens, usuario.getCpf());
        tokens = ArrayUtils.addAll(tokens, usuario.getCpf().replaceAll("[.-]", ""));
        tokens = ArrayUtils.addAll(tokens, StringUtils.split(usuario.getCpf(), ".-"));
        tokens = ArrayUtils.addAll(tokens, StringUtils.split(usuario.getNomeCompleto()));
        tokens = ArrayUtils.addAll(tokens, StringUtils.split(usuario.getEmail(), "@."));
        return tokens;
    }
}
