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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
public class CriptoConverter implements AttributeConverter<String, byte[]> {

    private static final String CHAVE = "DEZESEIS54123478";
    public static String IV = "AAAAAAAAAAAAAAAA";

    public static byte[] encrypt(String textopuro) throws Exception {
        Cipher encripta = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(CHAVE.getBytes("UTF-8"), "AES");
        encripta.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
        return encripta.doFinal(textopuro.getBytes("UTF-8"));
    }

    public static String decrypt(byte[] textoencriptado) throws Exception {
        Cipher decripta = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(CHAVE.getBytes("UTF-8"), "AES");
        decripta.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
        return new String(decripta.doFinal(textoencriptado), "UTF-8");
    }

    @Override
    public byte[] convertToDatabaseColumn(String x) {
        try {
            final byte[] criptografado = encrypt(x);
            return criptografado;
        } catch (Exception ex) {
            Logger.getLogger(CriptoConverter.class.getName()).log(Level.SEVERE, null, "Erro ao criptografar o valor " + x);
        }
        return null;
    }

    @Override
    public String convertToEntityAttribute(byte[] y) {
        try {
            return decrypt(y);
        } catch (Exception ex) {
            Logger.getLogger(CriptoConverter.class.getName()).log(Level.SEVERE, null, "Erro ao descriptografar o valor");
        }
        return null;
    }

}
