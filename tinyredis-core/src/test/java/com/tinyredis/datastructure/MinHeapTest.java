package com.tinyredis.datastructure;

import java.util.Comparator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class MinHeapTest {

    @Test
    void testNewHeapIsEmpty() {
        MinHeap<Integer> heap = new MinHeap<>(Integer::compareTo);
        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
    }

    @Test
    void testAddAndPeekSingleElement() {
        MinHeap<Integer> heap = new MinHeap<>(Integer::compareTo);
        heap.add(42);

        assertFalse(heap.isEmpty());
        assertEquals(1, heap.size());
        assertEquals(42, heap.peek());
        // Ensure peek does not remove the element
        assertEquals(1, heap.size());
    }

    @Test
    void testAddAndPollMultipleElementsInOrder() {
        MinHeap<Integer> heap = new MinHeap<>(Integer::compareTo);

        int[] values = {15, 10, 20, 1, 5, 30, 25, 3};
        for (int val : values) {
            heap.add(val);
        }

        assertEquals(8, heap.size());

        int[] expectedOrder = {1, 3, 5, 10, 15, 20, 25, 30};
        for (int expected : expectedOrder) {
            assertEquals(expected, heap.peek());
            assertEquals(expected, heap.poll());
        }

        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
    }

    @Test
    void testDuplicateElements() {
        MinHeap<Integer> heap = new MinHeap<>(Integer::compareTo);

        heap.add(5);
        heap.add(2);
        heap.add(5);
        heap.add(1);
        heap.add(2);

        assertEquals(5, heap.size());
        assertEquals(1, heap.poll());
        assertEquals(2, heap.poll());
        assertEquals(2, heap.poll());
        assertEquals(5, heap.poll());
        assertEquals(5, heap.poll());
        assertTrue(heap.isEmpty());
    }

    @Test
    void testSingleElementPoll() {
        MinHeap<String> heap = new MinHeap<>(String::compareTo);
        heap.add("hello");

        assertEquals("hello", heap.poll());
        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
    }

    @Test
    void testPeekOnEmptyHeapThrowsException() {
        MinHeap<Integer> heap = new MinHeap<>(Integer::compareTo);
        assertThrows(NoSuchElementException.class, heap::peek);
    }

    @Test
    void testPollOnEmptyHeapThrowsException() {
        MinHeap<Integer> heap = new MinHeap<>(Integer::compareTo);
        assertThrows(NoSuchElementException.class, heap::poll);
    }

    @Test
    void testCustomComparator() {
        // Max-heap behavior using reverse order comparator
        MinHeap<Integer> maxHeap = new MinHeap<>(Comparator.<Integer>reverseOrder());

        maxHeap.add(10);
        maxHeap.add(50);
        maxHeap.add(30);

        assertEquals(50, maxHeap.poll());
        assertEquals(30, maxHeap.poll());
        assertEquals(10, maxHeap.poll());
        assertTrue(maxHeap.isEmpty());
    }
}
