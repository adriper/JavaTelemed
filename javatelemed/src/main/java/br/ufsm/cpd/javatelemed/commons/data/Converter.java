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
import java.time.Month;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author mfonseca
 */
@FunctionalInterface
public interface Converter<IN, OUT> {

    OUT apply(IN obj);

    public static Converter<Object, LocalDate> toLocalDate() {
        return createConverter(
                (val) -> {
                    if (val == null || (val instanceof LocalDate)) {
                        return (LocalDate) val;
                    } else if (val instanceof java.sql.Date) {
                        return ((java.sql.Date) val).toLocalDate();
                    } else if (val instanceof java.util.Date) {
                        return LocalDateTime.ofInstant(((Date) val).toInstant(), ZoneId.systemDefault()).toLocalDate();
                    } else if (val instanceof Calendar) {
                        return LocalDateTime.ofInstant(((Calendar) val).toInstant(), ZoneId.systemDefault()).toLocalDate();
                    } else if (val instanceof LocalDateTime) {
                        return ((LocalDateTime) val).toLocalDate();
                    }
                    throw new IllegalArgumentException("Value type <" + val.getClass().getSimpleName() + "> not supportted");
                }
        );
    }

    public static Converter<Object, LocalTime> toLocalTime() {
        return createConverter(
                (val) -> {
                    if (val == null || (val instanceof LocalTime)) {
                        return (LocalTime) val;
                    } else if (val instanceof java.sql.Date) {
                        return LocalTime.MIDNIGHT;
                    } else if (val instanceof java.util.Date) {
                        return LocalDateTime.ofInstant(((Date) val).toInstant(), ZoneId.systemDefault()).toLocalTime();
                    } else if (val instanceof Calendar) {
                        return LocalDateTime.ofInstant(((Calendar) val).toInstant(), ZoneId.systemDefault()).toLocalTime();
                    } else if (val instanceof LocalDateTime) {
                        return ((LocalDateTime) val).toLocalTime();
                    }
                    throw new IllegalArgumentException("Value type <" + val.getClass().getSimpleName() + "> not supportted");
                }
        );
    }
    
    public static Converter<Object, LocalDateTime> toLocalDateTime() {
        return createConverter(
                (val) -> {
                    if (val == null || (val instanceof LocalDateTime)) {
                        return (LocalDateTime) val;
                    } else if (val instanceof Date) {
                        return LocalDateTime.ofInstant(((Date) val).toInstant(), ZoneId.systemDefault());
                    } else if (val instanceof Calendar) {
                        return LocalDateTime.ofInstant(((Calendar) val).toInstant(), ZoneId.systemDefault());
                    } else if (val instanceof LocalDate) {
                        return LocalDateTime.of((LocalDate) val, LocalTime.MIN);
                    } else if (val instanceof LocalTime) {
                        return LocalDateTime.of(LocalDate.of(1970, Month.JANUARY, 1), (LocalTime) val);
                    }
                    throw new IllegalArgumentException("Value type <" + val.getClass().getSimpleName() + "> not supportted");
                }
        );
    }

    public static <IN, OUT> Converter<IN, OUT> createConverter(final Function<IN, OUT> func) {
        Objects.requireNonNull(func, "Function cannot be null.");
        return (final IN obj) -> obj != null ? (OUT) func.apply(obj) : null;
    }
}
