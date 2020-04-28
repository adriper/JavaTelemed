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
package br.ufsm.cpd.javatelemed.persistence.service;

import br.ufsm.cpd.javatelemed.persistence.model.Profissional;
import br.ufsm.cpd.javatelemed.persistence.model.TokenRecuperacaoSenha;
import br.ufsm.cpd.javatelemed.utils.DateUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Giuliano Ferreira <giuliano@ufsm.br>
 */
@Transactional
public interface TokenRecuperacaoSenhaService extends CrudRepository<TokenRecuperacaoSenha, Long> {

    Optional<TokenRecuperacaoSenha> findByToken(final String token);

    @Query("select this from TokenRecuperacaoSenha this where this.ativo = true and this.profissional.id = :idProfissional")
    Collection<TokenRecuperacaoSenha> findAtivosByProfissional(final Long idProfissional);

    default TokenRecuperacaoSenha geraToken(final Profissional profissional, final Date validade) {
        final Collection<TokenRecuperacaoSenha> tokens = findAtivosByProfissional(profissional.getId());
        tokens.forEach(t -> t.setAtivo(Boolean.FALSE));
        saveAll(tokens);

        final LocalDateTime dataCriacao = LocalDateTime.now();
        final String conteudo = profissional.getCpf() + dataCriacao.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + profissional.getNomeCompleto();
        final String hash = DigestUtils.sha256Hex(conteudo);

        final TokenRecuperacaoSenha token = new TokenRecuperacaoSenha();
        token.setToken(hash);
        token.setAtivo(Boolean.TRUE);
        token.setProfissional(profissional);
        token.setDataCriacao(DateUtils.castToDate(dataCriacao));
        token.setDataValidade(validade);
        return save(token);
    }

    default TokenRecuperacaoSenha geraToken(final Profissional profissional) {
        final LocalDateTime dataCriacao = LocalDateTime.now();
        return geraToken(profissional, DateUtils.castToDate(dataCriacao.plusHours(4)));
    }

    default TokenRecuperacaoSenha invalida(String token) {
        final TokenRecuperacaoSenha tokenRecuperacaoSenha = findByToken(token)
                .filter(t -> t.isValido())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));
        tokenRecuperacaoSenha.setAtivo(Boolean.FALSE);
        return save(tokenRecuperacaoSenha);
    }
}
