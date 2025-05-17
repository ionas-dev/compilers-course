package edu.kit.kastel.vads.compiler.backend.common.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

public class Graph<T> {

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

    /**
     * Algorithm: Maximum cardinality search
     * @return A simplicial elimination ordered list of nodes
     */
    public List<Node<T>> simplicialEliminationOrder() {
        List<Node<T>> orderedNodes = new ArrayList<>(nodes.size());

        PriorityQueue<WeightedValue<T>> pq = new PriorityQueue<>(nodes.size(), Comparator.reverseOrder());
        nodes.values().stream().map(WeightedValue<T>::new).forEach(pq::add);

        for (int i = 0; i < nodes.size(); i++) {
            Node<T> maxNode = Objects.requireNonNull(pq.poll()).node;
            for (Node<T> neighbor: maxNode.getNeighbors()) {
                pq.stream().filter(weightedOperand -> weightedOperand.node.equals(neighbor)).forEach(weightedOperand -> weightedOperand.weight++);
            }
            orderedNodes.add(maxNode);
        }
        return orderedNodes;
    }

    private static class WeightedValue<T> implements Comparable<WeightedValue<T>> {

        private final Node<T> node;
        private int weight;

        public WeightedValue(Node<T> node) {
            this.node = node;
            this.weight = 0;
        }

        @Override
        public int compareTo(WeightedValue<T> o) {
            return weight - o.weight;
        }
    }
}
