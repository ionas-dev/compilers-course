package edu.kit.kastel.vads.compiler.backend.x86_64.regalloc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Node<T> {
    private final T value;

    private final Set<Node<T>> neighbors = new HashSet<>();

    public Node(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public Collection<Node<T>> getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(Node<T> neighbor) {
        neighbors.add(neighbor);
    }

    @Override
    public int hashCode() {
        return Node.class.hashCode() + value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass()) && this.hashCode() == obj.hashCode();
    }
}
