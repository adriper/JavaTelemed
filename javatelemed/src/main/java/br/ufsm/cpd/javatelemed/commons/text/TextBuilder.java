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

import br.ufsm.cpd.javatelemed.commons.data.Formatter;
import br.ufsm.cpd.javatelemed.commons.collection.CollectionUtils;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author mfonseca
 */
public class TextBuilder implements CharSequence, Comparable<TextBuilder> {

    public static final String DEF_NEWLINE = "\n";
    private String newLine = DEF_NEWLINE;

    protected final StringBuilder delegate;

    public TextBuilder() {
        this.delegate = new StringBuilder();
    }

    public TextBuilder setNewLine(final CharSequence ln) {
        newLine = (ln != null && ln.length() > 0 ? ln : DEF_NEWLINE).toString();
        return this;
    }

    public TextBuilder ln() {
        delegate.append(newLine);
        return this;
    }

    public TextBuilder when(final boolean condition, final Consumer<TextBuilder> then) {
        return when(condition, then, null);
    }

    public TextBuilder when(final boolean condition, final Consumer<TextBuilder> then, final Consumer<TextBuilder> otherwise) {
        if (condition && then != null) {
            then.accept(this);
        } else if (!condition && otherwise != null) {
            otherwise.accept(this);
        }
        return this;
    }

    public <T> TextBuilder forEach(final Collection<T> collection, final BiConsumer<TextBuilder, T> consumer) {
        CollectionUtils.streamOf(collection).forEach(item -> consumer.accept(this, item));
        return this;
    }

    public TextBuilder append(final Object... values) {
        return insert(CollectionUtils.streamOf(values), null);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public <T> TextBuilder append(final T value, final Formatter<T> formatter) {
        return insert(value != null ? Stream.of(value) : Stream.empty(), formatter);
    }

    public TextBuilder appendIf(final boolean condition,
            final Supplier<?> supplier) {
        return append(condition, supplier, null, null, null);
    }

    public <T> TextBuilder appendIf(final boolean condition,
            final Supplier<T> supplier,
            final Formatter<T> formatter) {
        return append(condition, supplier, null, formatter, null);
    }

    public <T, F> TextBuilder append(final boolean condition,
            final Supplier<T> trueSupplier,
            final Supplier<F> falseSupplier) {
        return append(condition, trueSupplier, falseSupplier, null, null);
    }

    public <T> TextBuilder append(final boolean condition,
            final Supplier<T> trueSupplier,
            final Supplier<T> falseSupplier,
            final Formatter<T> formatter) {
        return append(condition, trueSupplier, falseSupplier, formatter, formatter);
    }

    public <T, F> TextBuilder append(final boolean condition,
            final Supplier<T> trueSupplier,
            final Supplier<F> falseSupplier,
            final Formatter<T> trueFormatter,
            final Formatter<F> falseFormatter) {
        if (condition && trueSupplier != null) {
            return append(trueSupplier.get(), trueFormatter);
        }
        if (!condition && falseSupplier != null) {
            return append(falseSupplier.get(), falseFormatter);
        }
        return this;
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public TextBuilder appendln(final Object... values) {
        return append(values).ln();
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public <T> TextBuilder appendln(final T value, final Formatter<T> formatter) {
        return append(value, formatter).ln();
    }

    public TextBuilder appendlnIf(final boolean condition,
            final Supplier<?> supplier) {
        return appendln(condition, supplier, null, null, null);
    }

    public <T> TextBuilder appendlnIf(final boolean condition,
            final Supplier<T> supplier,
            final Formatter<T> formatter) {
        return appendln(condition, supplier, null, formatter, null);
    }

    public <T, F> TextBuilder appendln(final boolean condition,
            final Supplier<T> trueSupplier,
            final Supplier<F> falseSupplier) {
        return appendln(condition, trueSupplier, falseSupplier, null, null);
    }

    public <T> TextBuilder appendln(final boolean condition,
            final Supplier<T> trueSupplier,
            final Supplier<T> falseSupplier,
            final Formatter<T> formatter) {
        return appendln(condition, trueSupplier, falseSupplier, formatter, formatter);
    }

    public <T, F> TextBuilder appendln(final boolean condition,
            final Supplier<T> trueSupplier,
            final Supplier<F> falseSupplier,
            final Formatter<T> trueFormatter,
            final Formatter<F> falseFormatter) {
        if (condition && trueSupplier != null) {
            return append(trueSupplier.get(), trueFormatter).ln();
        }
        if (!condition && falseSupplier != null) {
            return append(falseSupplier.get(), falseFormatter).ln();
        }
        return this;
    }

    public boolean isEmpty() {
        return length() == 0;
    }

    public boolean isNotEmpty() {
        return length() > 0;
    }

    public boolean isBlank() {
        return length() == 0
                || Pattern.compile("\\s*").matcher(this.delegate).matches();
    }

    public boolean isNotBlank() {
        return !isBlank();
    }

    @Override
    public int length() {
        return this.delegate.length();
    }

    @Override
    public char charAt(final int index) {
        return this.delegate.charAt(index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return this.delegate.subSequence(start, end);
    }

    @Override
    public int compareTo(final TextBuilder o) {
        return this.delegate.toString().compareTo(Optional.ofNullable(delegate).map(v -> v.toString()).orElse(null));
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    protected <T> TextBuilder insert(final Stream<T> stream, final Formatter<T> formatter) {
        final String formated = stream
                .map((v) -> Formatter.nullsafe(formatter).applyOptional(v).orElse(""))
                .filter((txt) -> !txt.isEmpty())
                .collect(Collectors.joining());
        delegate.append(formated);
        return this;
    }
}
