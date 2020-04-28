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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author Adriano Pereira <adriano@ufsm.br>
 */
public class DateUtils {

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH)
                || (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    public static Date castToDate(final Object date) {
        if (date == null) {
            return null;
        } else if (date instanceof Calendar) {
            return ((Calendar) date).getTime();
        } else if (date instanceof Date) {
            return (Date) date;
        } else if (date instanceof LocalDate) {
            return Date.from(((LocalDate) date).atStartOfDay(ZoneId.systemDefault()).toInstant());
        } else if (date instanceof LocalDateTime) {
            return Date.from(((LocalDateTime) date).atZone(ZoneId.systemDefault()).toInstant());
        } else {
            throw new IllegalArgumentException("Argument must be Date or Calendar");
        }
    }

    public static LocalDate castToLocalDate(final Object date) {
        Date d = null;
        if (date == null) {
            return null;
        } else if (date instanceof Calendar) {
            d = ((Calendar) date).getTime();
        } else if (date instanceof java.sql.Time) {
            return LocalDate.now();
        } else if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        } else if (date instanceof Date) {
            d = (Date) date;
        } else {
            throw new IllegalArgumentException("Argument must be Date or Calendar");
        }
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime castToLocalDateTime(final Object date) {
        Date d = null;
        if (date == null) {
            return null;
        } else if (date instanceof Calendar) {
            d = ((Calendar) date).getTime();
        } else if (date instanceof java.sql.Time) {
            return LocalDateTime.of(LocalDate.now(), ((java.sql.Time) date).toLocalTime());
        } else if (date instanceof java.sql.Date) {
            return LocalDateTime.of(((java.sql.Date) date).toLocalDate(), LocalTime.now());
        } else if (date instanceof Date) {
            d = (Date) date;
        } else {
            throw new IllegalArgumentException("Argument must be Date or Calendar");
        }
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }
}
