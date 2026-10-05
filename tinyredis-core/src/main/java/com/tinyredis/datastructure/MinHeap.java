package com.tinyredis.datastructure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

public class MinHeap<T> {

    private final List<T> heap;
    private final Comparator<T> comparator;

    public MinHeap(Comparator<T> comparator) {
        this.heap = new ArrayList<>();
        this.comparator = comparator;
    }

    public void add(T value) {
        heap.add(value);
        heapifyUp(heap.size() - 1);
    }

    public T peek() {
        if (heap.isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }

        return heap.getFirst();
    }

    public T poll() {
        if (heap.isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }

        T element = heap.getFirst();

        if (heap.size() == 1) {
            heap.removeFirst();
            return element;
        }

        swap(0, heap.size() - 1);
        heap.remove(heap.size() - 1);
        heapifyDown(0);

        return element;
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public int size() {
        return heap.size();
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;

            if (comparator.compare(heap.get(index), heap.get(parent)) >= 0) {
                break;
            }

            swap(index, parent);

            index = parent;
        }
    }

    private void heapifyDown(int index) {
        int size = heap.size();

        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;

            if (left >= size) {
                break;
            }

            int smallerChild = left;

            if (right < size && comparator.compare(heap.get(right), heap.get(left)) < 0) {
                smallerChild = right;
            }

            if (comparator.compare(heap.get(index), heap.get(smallerChild)) <= 0) {
                break;
            }

            swap(index, smallerChild);

            index = smallerChild;
        }
    }

    private void swap(int first, int second) {
        T temp = heap.get(first);
        heap.set(first, heap.get(second));
        heap.set(second, temp);
    }
}
