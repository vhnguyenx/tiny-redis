package com.tinyredis.eviction;

import java.util.HashMap;
import java.util.Map;

import com.tinyredis.datastructure.DoublyLinkedList;

public class EvictionManager {

    private final DoublyLinkedList<String> lruList;
    private final Map<String, DoublyLinkedList.Node<String>> nodeMap;

    public EvictionManager() {
        this.lruList = new DoublyLinkedList<>();
        this.nodeMap = new HashMap<>();
    }

    public void onInsert(String key) {
        if (nodeMap.containsKey(key)) {
            onAccess(key);
            return;
        }

        lruList.addFirst(key);
        nodeMap.put(key, lruList.getFirstNode());
    }

    public void onAccess(String key) {
        DoublyLinkedList.Node<String> node = nodeMap.get(key);

        if (node == null) {
            return;
        }

        lruList.moveToFront(node);
    }

    public void onRemove(String key) {
        DoublyLinkedList.Node<String> node = nodeMap.remove(key);

        if (node == null) {
            return;
        }

        lruList.remove(node);
    }

    public String peekVictim() {
        return lruList.peekLast();
    }

    public String evict() {
        String victim = lruList.removeLast();

        nodeMap.remove(victim);

        return victim;
    }

    public int size() {
        return nodeMap.size();
    }

    public boolean contains(String key) {
        return nodeMap.containsKey(key);
    }
}
