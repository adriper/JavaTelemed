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
package br.ufsm.cpd.javatelemed.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
public class RecaptchaUtils {

    private static final String URL_GOOGLE_RECAPTCHA = "https://www.google.com/recaptcha/api/siteverify";
    private static final String TOKEN_PRIVATE = "";

    public static final boolean recaptcha(final String tokenUserResponse) {
        final RestTemplate restTemplate = new RestTemplate();
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>(2);
        params.add("secret", TOKEN_PRIVATE);
        params.add("response", tokenUserResponse);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        final HttpEntity<MultiValueMap<String, String>>request = new HttpEntity<MultiValueMap<String, String>>(params, headers);
        final ResponseEntity<ResponseRecaptcha> response = restTemplate.postForEntity(URL_GOOGLE_RECAPTCHA, request, ResponseRecaptcha.class);
        return (response != null && response.getBody().getSuccess());
    }

    private static final class ResponseRecaptcha {

        private Boolean success;

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public ResponseRecaptcha() {
        }
    }

    private static final class ArgumentsRecaptcha {

        private String secret;
        private String response;

        public ArgumentsRecaptcha() {
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        public ArgumentsRecaptcha(String secret, String response) {
            this.secret = secret;
            this.response = response;
        }

    }

}
