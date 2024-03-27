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
import be.darkkraft.concurrentunique.generator.FakeIntegerGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FullCacheGeneratorTest {

    @Test
    void testAlreadyExists() {
        final FakeIntegerGenerator fakeGenerator = new FakeIntegerGenerator(new int[] {1, 2, 3});
        final UniqueGenerator<Integer> generator = new SequentialFullCacheGenerator<>(fakeGenerator, 5);

        assertEquals(1, generator.generate());
        assertEquals(2, generator.generate());
        assertEquals(3, generator.generate());
        assertNull(generator.generate());
    }

    @Test
    void testPurge() {
        final FakeIntegerGenerator fakeGenerator = new FakeIntegerGenerator(new int[] {1});
        final FullCacheGenerator<Integer> generator = new SequentialFullCacheGenerator<>(fakeGenerator, -1);

        assertEquals(1, generator.generate());
        assertNull(generator.generate());
        generator.purge();
        assertEquals(1, generator.generate());
        assertNull(generator.generate());
    }

    @Test
    void testAlwaysNull() {
        final UniqueGenerator<Integer> generator = new SequentialFullCacheGenerator<>(UniqueGenerator.empty(), 4);
        for (int i = 0; i < 10; i++) {
            assertNull(generator.generate());
        }
    }

}