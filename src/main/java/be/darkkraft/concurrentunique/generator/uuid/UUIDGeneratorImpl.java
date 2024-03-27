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

package be.darkkraft.concurrentunique.generator.uuid;

import be.darkkraft.concurrentunique.supplier.RandomGeneratorSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

record UUIDGeneratorImpl(@NotNull RandomGeneratorSupplier randomGeneratorSupplier) implements UUIDGenerator {

    UUIDGeneratorImpl {
        Objects.requireNonNull(randomGeneratorSupplier, "randomGeneratorSupplier must not be null");
    }

    @Override
    public UUID generate() {
        final byte[] bytes = new byte[16];
        this.randomGeneratorSupplier.getRandomGenerator().nextBytes(bytes);
        bytes[6] &= 0x0f;
        bytes[6] |= 0x40;
        bytes[8] &= 0x3f;
        bytes[8] |= (byte) 0x80;

        long msb = 0, lsb = 0;
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (bytes[i] & 0xff);
        }
        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (bytes[i] & 0xff);
        }

        return new UUID(msb, lsb);
    }

    @Override
    public @NotNull RandomGeneratorSupplier getRandomGeneratorSupplier() {
        return this.randomGeneratorSupplier;
    }

}