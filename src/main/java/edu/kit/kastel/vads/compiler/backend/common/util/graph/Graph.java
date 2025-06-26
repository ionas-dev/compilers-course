package edu.kit.kastel.vads.compiler.backend.common.util.graph;

import edu.kit.kastel.vads.compiler.backend.common.util.FibonacciHeap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

        FibonacciHeap<Node<T>> heap = new FibonacciHeap<>();
        Map<Node<T>, FibonacciHeap.Entry<Node<T>>> entries = new HashMap<>(nodes.size());
        nodes.values().forEach(node -> entries.put(node, heap.enqueue(node, nodes.size())));

        while (!heap.isEmpty()) {
            Node<T> maxNode = heap.dequeueMin().getValue();
            maxNode.getNeighbors().stream().map(entries::get).filter(Objects::nonNull).forEach(neighbor -> heap.decreaseKey(neighbor, neighbor.getPriority() - 1));
            orderedNodes.add(maxNode);
            entries.remove(maxNode);
        }
        return orderedNodes;
    }

    /**
     * Algorithm: Maximum cardinality search
     * @return A simplicial elimination ordered list of nodes
     */
    public List<Node<T>> simplicialEliminationOrder2() {
        List<Node<T>> orderedNodes = new ArrayList<>(nodes.size());

        Map<Node<T>, Integer> weightedNodes = new HashMap<>(nodes.size());
        nodes.values().forEach(node -> weightedNodes.put(node, 0));

        if (weightedNodes.isEmpty()) {
            return orderedNodes;
        }

        while (!weightedNodes.isEmpty()) {
            Map.Entry<Node<T>, Integer> maxEntry = weightedNodes.entrySet().stream()
                    .reduce(weightedNodes.entrySet().stream().findFirst().get(), (Map.Entry<Node<T>, Integer> a, Map.Entry<Node<T>, Integer> b) -> a.getValue().compareTo(b.getValue()) >= 0 ? a : b);
            maxEntry.getKey().getNeighbors().stream().filter(weightedNodes::containsKey).forEach(neighbor -> weightedNodes.put(neighbor, weightedNodes.get(neighbor) + 1));
            orderedNodes.add(maxEntry.getKey());
            weightedNodes.remove(maxEntry.getKey());
        }
        return orderedNodes;
    }
}
