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

package be.darkkraft.concurrentunique.supplier;

import be.darkkraft.concurrentunique.random.SequentialSeedRandom;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

public interface RandomGeneratorSupplier {

    @Contract("_ -> new")
    static @NotNull RandomGeneratorSupplier build(final @NotNull Type type) {
        return Objects.requireNonNull(type, "type must not be null").build();
    }

    @Contract("_ -> new")
    static @NotNull RandomGeneratorSupplier wrap(final @NotNull RandomGenerator randomGenerator) {
        return new WrappedGeneratorSupplier(randomGenerator);
    }

    @NotNull RandomGenerator getRandomGenerator();

    enum Type {

        FAST_SEQUENTIAL(() -> wrap(new SequentialSeedRandom())),
        SECURE(() -> wrap(new SecureRandom())),
        THREAD_LOCAL(() -> wrap(ThreadLocalRandom.current()));

        private final Supplier<RandomGeneratorSupplier> supplier;

        Type(final @NotNull Supplier<RandomGeneratorSupplier> supplier) {
            this.supplier = Objects.requireNonNull(supplier, "supplier must not be null");
        }

        private RandomGeneratorSupplier build() {
            return this.supplier.get();
        }

    }

}