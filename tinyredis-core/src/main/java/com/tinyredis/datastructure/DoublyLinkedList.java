package com.tinyredis.datastructure;

import java.util.NoSuchElementException;

public class DoublyLinkedList<T> {

    public static class Node<T> {
        private final T value;
        private Node<T> prev;
        private Node<T> next;

        public Node(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public void addFirst(T value) {
        Node<T> node = new Node<>(value);

        if (head == null) {
            head = node;
            tail = node;
            size++;
            return;
        }

        node.next = head;
        head.prev = node;
        head = node;

        size++;
    }

    public void addLast(T value) {
        Node<T> node = new Node<>(value);

        if (tail == null) {
            head = node;
            tail = node;
            size++;
            return;
        }

        node.prev = tail;
        tail.next = node;
        tail = node;

        size++;
    }

    public T removeFirst() {
        if (head == null) {
            throw new NoSuchElementException("List is empty");
        }

        Node<T> removed = head;

        if (head == tail) {
            head = null;
            tail = null;
        } else {
            head = head.next;
            head.prev = null;
        }

        size--;
        removed.next = null;

        return removed.value;
    }

    public T removeLast() {
        if (tail == null) {
            throw new NoSuchElementException("List is empty");
        }

        Node<T> removed = tail;

        if (tail == head) {
            head = null;
            tail = null;
        } else {
            tail = tail.prev;
            tail.next = null;
        }

        size--;

        removed.prev = null;

        return removed.value;
    }

    public void remove(Node<T> node) {
        if (node == null) {
            return;
        }

        if (node == head) {
            removeFirst();
            return;
        }

        if (node == tail) {
            removeLast();
            return;
        }

        Node<T> previous = node.prev;
        Node<T> next = node.next;

        previous.next = next;
        next.prev = previous;

        node.prev = null;
        node.next = null;

        size--;
    }

    public void moveToFront(Node<T> node) {
        if (node == null || node == head) {
            return;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        }

        if (node == tail) {
            tail = node.prev;
        }

        node.prev = null;
        node.next = head;

        head.prev = node;
        head = node;
    }

    public T peekFirst() {
        if (head == null) {
            throw new NoSuchElementException("List is empty");
        }

        return head.value;
    }

    public T peekLast() {
        if (tail == null) {
            throw new NoSuchElementException("List is empty");
        }

        return tail.value;
    }

    public Node<T> getFirstNode() {
        return head;
    }

    public Node<T> getLastNode() {
        return tail;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }
}
