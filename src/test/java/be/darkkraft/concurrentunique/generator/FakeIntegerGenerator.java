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

package be.darkkraft.concurrentunique.generator;

import be.darkkraft.concurrentunique.UniqueGenerator;

import java.util.Objects;
import java.util.stream.IntStream;

public final class FakeIntegerGenerator implements UniqueGenerator<Integer> {

    private final int[] planned;
    private int index;

    public FakeIntegerGenerator() {
        this(IntStream.range(0, 100).toArray());
    }

    public FakeIntegerGenerator(final int[] planned) {
        this.planned = Objects.requireNonNull(planned, "planned numbers cannot be null");
    }

    @Override
    public Integer generate() {
        final int value = this.planned[this.index++];
        if (this.index == this.planned.length) {
            this.index = 0;
        }
        return value;
    }

}