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
package br.ufsm.cpd.javatelemed.commons.text;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 *
 * @author mfonseca
 */
public class TextUtils {

    public static final String UPPERCASES_REGEX = "\\p{Lu}+";
    public static final String LOWERCASES_REGEX = "\\p{Ll}+";
    public static final String LETTERS_REGEX = "\\p{L}+";
    public static final String DIGITS_REGEX = "\\d+";
    public static final String SIMBOLS_REGEX = "[\\p{P}\\p{S}]+";
    public static final String SPACES_REGEX = "\\s+";

    public static boolean isBlankOrNull(final CharSequence val) {
        if (val == null || val.length() == 0) {
            return true;
        }
        return Pattern.compile("\\s*").matcher(val).matches();
    }

    public static boolean isNotBlank(final CharSequence val) {
        return !isBlankOrNull(val);
    }

    public static int length(final CharSequence val) {
        return Optional.ofNullable(val).map(CharSequence::length).orElse(0);
    }

    public static <T extends CharSequence> Optional<T> optionalOf(final T val) {
        return isBlankOrNull(val) ? Optional.empty() : Optional.of(val);
    }

    public static <T extends CharSequence> T notBlankOrElse(final T val, final T def) {
        return optionalOf(val).orElse(def);
    }

    public static <T extends CharSequence> T notBlankOrElse(final T val, final Supplier<T> def) {
        return optionalOf(val).orElseGet(def);
    }

    public static <T extends CharSequence> T notBlankOrThrow(final T val, final Supplier<? extends RuntimeException> def) {
        return optionalOf(val).orElseThrow(def);
    }

    public static boolean hasUppercase(final CharSequence sequence) {
        return isNotBlank(sequence) ? containsPattern(UPPERCASES_REGEX, sequence) : false;
    }

    public static boolean hasLowercase(final CharSequence sequence) {
        return isNotBlank(sequence) ? containsPattern(LOWERCASES_REGEX, sequence) : false;
    }

    public static boolean hasLetter(final CharSequence sequence) {
        return isNotBlank(sequence) ? containsPattern(LETTERS_REGEX, sequence) : false;
    }

    public static boolean hasDigit(final CharSequence sequence) {
        return isNotBlank(sequence) ? containsPattern(DIGITS_REGEX, sequence) : false;
    }

    public static boolean hasSymbol(final CharSequence sequence) {
        return isNotBlank(sequence) ? containsPattern(SIMBOLS_REGEX, sequence) : false;
    }

    public static boolean hasSpace(final CharSequence sequence) {
        return isNotBlank(sequence) ? containsPattern(SPACES_REGEX, sequence) : false;
    }

    public static boolean matches(final CharSequence regex, final CharSequence sequence) {
        return matches(regex, sequence, 0);
    }

    public static boolean matches(final CharSequence regex, final CharSequence sequence, final int flags) {
        final Pattern pattern = Pattern.compile(regex.toString(), flags);
        return pattern.matcher(sequence).matches();
    }

    public static boolean containsPattern(final CharSequence regex, final CharSequence sequence) {
        final Pattern pattern = Pattern.compile(regex.toString());
        return pattern.matcher(sequence).find();
    }

}
