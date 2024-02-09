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

package be.darkkraft.concurrentunique.random;

import java.util.random.RandomGenerator;

public final class SequentialSeedRandom implements RandomGenerator {

    private long seed;

    public SequentialSeedRandom() {
        this(System.currentTimeMillis());
    }

    public SequentialSeedRandom(final long seed) {
        this.seed = seed;
    }

    @Override
    public long nextLong() {
        return mix64(this.seed += 0x9e3779b97f4a7c15L);
    }

    public int randomInt(final int min, final int max) {
        final int bound = (max + 1) - min;
        int r = this.next(31);
        final int m = bound - 1;
        if ((bound & m) == 0) {
            r = (int) ((bound * (long) r) >> 31);
        } else {
            int u = r;
            while (u - (r = u % bound) + m < 0) {
                u = this.next(31);
            }
        }
        return min + r;
    }

    public int next(final int bits) {
        return (int) ((this.seed = (this.seed * 25214903917L + 11) & 281474976710655L) >>> (48 - bits));
    }

    private static long mix64(long z) {
        z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9L;
        z = (z ^ (z >>> 27)) * 0x94d049bb133111ebL;
        return z ^ (z >>> 31);
    }

}