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
package br.ufsm.cpd.javatelemed.commons.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 *
 * @author mfonseca
 * @param <IN>
 */
@FunctionalInterface
public interface Formatter<IN> extends Converter<IN, String> {

    @Override
    String apply(IN obj);
    
    default Optional<String> applyOptional(final IN obj) {
        final String str = apply(obj);
        return (str == null || str.trim().isEmpty()) ? Optional.empty() : Optional.of(str);
    }

    default boolean supports() {
        return false;
    }

    public static String format(final Object val) {
        return getDefault().apply(val);
    }

    public static Optional<String> formatOptional(final Object val) {
        return Optional.ofNullable(format(val));
    }

    public static Formatter<Object> nullsafe(final Formatter f) {
        return f != null ? f : getDefault();
    }

    public static Formatter<Object> getDefault() {
        return createFormatter(
                (val) -> {
                    if ((val instanceof Date) || (val instanceof Calendar) || (val instanceof LocalDateTime)) {
                        return Formatter.ofDateTime().apply(val);
                    } else if (val instanceof LocalDate) {
                        return Formatter.ofDate().apply(val);
                    } else if (val instanceof LocalTime) {
                        return Formatter.ofTime().apply(val);
                    }
                    return Objects.toString(val, null);
                }
        );
    }

    public static Formatter<Object> ofDate() {
        return ofDate(null);
    }

    public static Formatter<Object> ofDate(final Locale locale) {
        return createFormatter(
                (val) -> {
                    final DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                            .withLocale(locale != null ? locale : Locale.getDefault(Locale.Category.FORMAT));
                    return fmt.format(Converter.toLocalDate().apply(val));
                }
        );
    }

    public static Formatter<Object> ofTime() {
        return ofTime(null);
    }

    public static Formatter<Object> ofTime(final Locale locale) {
        return createFormatter(
                (val) -> {
                    final DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                            .withLocale(locale != null ? locale : Locale.getDefault(Locale.Category.FORMAT));
                    return fmt.format(Converter.toLocalTime().apply(val));
                }
        );
    }

    public static Formatter<Object> ofDateTime() {
        return ofDateTime(null, null);
    }

    public static Formatter<Object> ofDateTime(final String pattern) {
        return ofDateTime(pattern, null);
    }

    public static Formatter<Object> ofDateTime(final Locale locale) {
        return ofDateTime(null, locale);
    }

    public static Formatter<Object> ofDateTime(final String pattern, final Locale locale) {
        return createFormatter(
                (val) -> {
                    final DateTimeFormatter fmt = ((pattern != null && !pattern.isEmpty())
                                                   ? DateTimeFormatter.ofPattern(pattern)
                                                   : DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
                            .withLocale(locale != null ? locale : Locale.getDefault(Locale.Category.FORMAT));
                    return fmt.format(Converter.toLocalDateTime().apply(val));
                }
        );
    }

    public static <IN> Formatter<IN> createFormatter(final Function<IN, String> func) {
        Objects.requireNonNull(func, "Function cannot be null.");
        return (final IN obj) -> obj != null ? (String) func.apply(obj) : null;
    }
}
