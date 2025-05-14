package edu.kit.kastel.vads.compiler.backend.x86_64.regalloc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

public class Graph<T> {

    // TODO: Maybe change to set
    Map<T, Node<T>> nodes = new HashMap<>();

    public Node<T> add(T value) {
        nodes.computeIfAbsent(value, _ -> new Node<>(value));
        return nodes.get(value);
    }

    public void addEdge(T value, T otherValue) {
        Node<T> node = add(value);
        Node<T> otherNode = add(otherValue);
        node.addNeighbor(otherNode);
        otherNode.addNeighbor(node);
    }

    public Collection<Node<T>> nodes() {
        return nodes.values();
    }
}
