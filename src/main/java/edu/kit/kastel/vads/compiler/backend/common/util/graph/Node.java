package edu.kit.kastel.vads.compiler.backend.common.util.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return Objects.equals(value, node.value) && Objects.equals(neighbors, node.neighbors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
