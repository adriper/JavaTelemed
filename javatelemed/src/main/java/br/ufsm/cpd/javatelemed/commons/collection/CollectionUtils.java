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
package br.ufsm.cpd.javatelemed.commons.collection;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author mfonseca
 */
public class CollectionUtils {

    public static int size(final Collection<?> val) {
        return Optional.ofNullable(val).map(Collection::size).orElse(0);
    }

    public static boolean isEmpty(final Collection<?> val) {
        return Optional.ofNullable(val).map(Collection::isEmpty).orElse(false);
    }

    public static boolean isEmptyOrNull(final Collection<?> val) {
        return Optional.ofNullable(val).map(Collection::isEmpty).orElse(true);
    }

    public static boolean isNotEmpty(final Collection<?> val) {
        return Optional.ofNullable(val).map(v -> !v.isEmpty()).orElse(false);
    }

    public static boolean isEmpty(final Map<?, ?> val) {
        return Optional.ofNullable(val).map(Map::isEmpty).orElse(false);
    }

    public static boolean isEmptyOrNull(final Map<?, ?> val) {
        return Optional.ofNullable(val).map(Map::isEmpty).orElse(true);
    }

    public static boolean isNotEmpty(final Map<?, ?> val) {
        return Optional.ofNullable(val).map(v -> !v.isEmpty()).orElse(false);
    }

    public static <T> Stream<T> streamOf(final Collection<T> val) {
        return Optional.ofNullable(val).map(Collection::stream).orElseGet(Stream::empty);
    }

    public static <K, V> Stream<Entry<K, V>> streamOf(final Map<K, V> val) {
        return Optional.ofNullable(val).map((v) -> v.entrySet().stream()).orElseGet(Stream::empty);
    }

    public static <K, V> Stream<K> keyStreamOf(final Map<K, V> val) {
        return Optional.ofNullable(val).map((v) -> v.keySet().stream()).orElseGet(Stream::empty);
    }

    public static <K, V> Stream<V> valueStreamOf(final Map<K, V> val) {
        return Optional.ofNullable(val).map((v) -> v.values().stream()).orElseGet(Stream::empty);
    }

    public static <T> Stream<T> streamOf(final T[] val) {
        return Optional.ofNullable(val).map(Stream::of).orElseGet(Stream::empty);
    }

    public static <T> Set<T> nullsafe(final Set<T> val) {
        return Optional.ofNullable(val).orElseGet(LinkedHashSet<T>::new);
    }

    public static <T> List<T> nullsafe(final List<T> val) {
        return Optional.ofNullable(val).orElseGet(LinkedList<T>::new);
    }

    public static <T> Collection<T> nullsafe(final Collection<T> val) {
        return Optional.ofNullable(val).orElseGet(LinkedList<T>::new);
    }

    public static <K, V> Map<K, V> nullsafe(final Map<K, V> val) {
        return Optional.ofNullable(val).orElseGet(LinkedHashMap<K, V>::new);
    }

    public static <T> Stream<T> nullsafe(final Stream<T> val) {
        return Optional.ofNullable(val).orElseGet(Stream::empty);
    }
}
