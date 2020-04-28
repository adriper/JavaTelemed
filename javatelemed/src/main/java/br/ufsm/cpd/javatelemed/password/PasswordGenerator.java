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
package br.ufsm.cpd.javatelemed.password;

import java.util.Random;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Um gerador de senhas.
 *
 * @author Marcius da Silva da Fonseca (mfonseca@ufsm.br)
 * @version 1.0
 */
public class PasswordGenerator {

    private static final Random RANDOM = new Random();

    /**
     * Generates a password of given length, without repeat the same character class sequentially.
     *
     * @param len The length of the desired password.
     * @return The new password.
     */
    public static String generate(final int len) {
        PasswordCharClass[] passwdPattern = new PasswordCharClass[len];
        for (int i = 0; i < len; i++) {
            passwdPattern[i] = (i == 0) ? randomCharacterClass() : randomCharacterClass(passwdPattern[i - 1]);
        }
        return generate(passwdPattern);
    }

    /**
     * Generates a password consisting by the given sequence of character classes.
     * <p/>
     * The length of the password will be the quantity of character classes given.
     *
     * @param passwdPattern The sequence of character classes that defines the new password creation criteria.
     * @return The new password.
     */
    public static String generate(final PasswordCharClass... passwdPattern) {
        if (ArrayUtils.isEmpty(passwdPattern)) {
            throw new IllegalArgumentException("Argument can't be empty");
        } else if (passwdPattern.length < 3) {
            throw new IllegalArgumentException("Argument can't be < 3");
        }
        StringBuilder builder = new StringBuilder(passwdPattern.length);
        for (PasswordCharClass ptn : passwdPattern) {
            builder.append(randomCharacter(ptn));
        }
        return builder.toString();
    }

    /**
     * Randomly selects a character class.
     *
     * @return the character class selected.
     */
    protected static PasswordCharClass randomCharacterClass() {
        return randomCharacterClass(null);
    }

    /**
     * Randomly selects a character class, excluding the given one.
     *
     * @param exclude A character class that will be excluded from the raffle.
     * @return The randomly selected character class.
     */
    protected static PasswordCharClass randomCharacterClass(final PasswordCharClass exclude) {
        PasswordCharClass[] values = PasswordCharClass.values();
        PasswordCharClass selected = values[RANDOM.nextInt(values.length)];
        if (exclude != null) {
            while (exclude.equals(selected)) {
                selected = values[RANDOM.nextInt(values.length)];
            }
        }
        return selected;
    }

    /**
     * Randomly selects a character that belongs to the given character class.
     *
     * @param characterClass The raffle character class.
     * @return The selected character.
     */
    protected static Character randomCharacter(final PasswordCharClass characterClass) {
        String validChars = characterClass.getValidChars();
        return validChars.charAt(RANDOM.nextInt(validChars.length()));
    }

    public enum PasswordCharClass {
        /**
         * All the uppercase (non-accented) letters.
         */
        UPPERCASE_LETTER("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
        /**
         * All the lowercase (non-accented) letters.
         */
        LOWERCASE_LETTER("abcdefghijklmnopqrstuvwxyz"),
        /**
         * All decimal digits.
         */
        DIGIT("0123456789"),
        /**
         * The accepted symbols.
         */
        SYMBOL("!@#$%&*+-=:?_.,");
        /**
         * The chars accepted by the character class.
         */
        private final String validChars;

        /**
         * Creates a new CharacterClass informing its accepted chars.
         *
         * @param validChars
         */
        private PasswordCharClass(final String validChars) {
            this.validChars = validChars;
        }

        /**
         * Returns the chars accepted by the character class.
         *
         * @return The chars accepted by the character class.
         */
        public String getValidChars() {
            return validChars;
        }
    }
}
