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

package be.darkkraft.concurrentunique.verified.cache;

import be.darkkraft.concurrentunique.UniqueGenerator;
import be.darkkraft.concurrentunique.verified.VerifiedGenerator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Objects;
import java.util.Set;

abstract sealed class FullCacheGenerator<T> implements VerifiedGenerator<T> permits ConcurrentFullCacheGenerator, SequentialFullCacheGenerator {

    private final UniqueGenerator<T> delegate;
    private final Set<T> keys;

    private int maxRetry;

    FullCacheGenerator(final @NotNull UniqueGenerator<T> delegate, final @NotNull Set<T> keys, final int maxRetry) {
        this.delegate = Objects.requireNonNull(delegate, "generator must not be null");
        this.keys = Objects.requireNonNull(keys, "keys must not be null");
        this.maxRetry = maxRetry;
    }

    @Override
    public T regenerate() {
        return this.delegate.generate();
    }

    @Override
    public T generate(final int maxRetry) {
        final T generated = VerifiedGenerator.super.generate(maxRetry);
        if (generated != null) {
            this.keys.add(generated);
        }
        return generated;
    }

    @Override
    public boolean isAlreadyExists(final @NotNull T generated) {
        return this.keys.contains(generated);
    }

    @Override
    public int getMaxRetry() {
        return this.maxRetry;
    }

    public void setMaxRetry(final int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public void purge() {
        this.keys.clear();
    }

    @Unmodifiable
    @Contract(pure = true)
    public @NotNull Set<T> getKeys() {
        return Set.copyOf(this.keys);
    }

}