/*
 * MIT License
 *
 * Copyright (c) 2024 Darkkraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.darkkraft.concurrentunique;

import be.darkkraft.concurrentunique.verified.VerifiedGenerator;
import be.darkkraft.concurrentunique.verified.cache.ConcurrentFullCacheGenerator;
import be.darkkraft.concurrentunique.verified.cache.SequentialFullCacheGenerator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface UniqueGenerator<T> {

    static <T> @NotNull UniqueGenerator<T> empty() {
        return EmptyGenerator.get();
    }

    T generate();

    default <R> R compute(final @NotNull Function<T, R> function) {
        final T generated = this.generate();
        return function.apply(generated);
    }

    @Contract("_ -> new")
    default <R> @NotNull UniqueGenerator<R> map(final @NotNull Function<T, R> function) {
        return new ChainedMapGenerator<>(this, function);
    }

    @Contract("-> new")
    default @NotNull UniqueGenerator<T> synchronize() {
        return new ChainedSynchronizedGenerator<>(this);
    }

    @Contract("-> new")
    default @NotNull Stream<T> stream() {
        return Stream.generate(this::generate);
    }

    @Contract("_, _ -> new")
    default @NotNull VerifiedGenerator<T> toVerified(final int maxRetry, final @NotNull Predicate<T> existPredicate) {
        return VerifiedGenerator.wrap(this, maxRetry, existPredicate);
    }

    @Contract("_ -> new")
    default @NotNull VerifiedGenerator<T> toSequentialCacheVerified(final int maxRetry) {
        return new SequentialFullCacheGenerator<>(this, maxRetry);
    }

    @Contract("_ -> new")
    default @NotNull VerifiedGenerator<T> toConcurrentCacheVerified(final int maxRetry) {
        return new ConcurrentFullCacheGenerator<>(this, maxRetry);
    }

}