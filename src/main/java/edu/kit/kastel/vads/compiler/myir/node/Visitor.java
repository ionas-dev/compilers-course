package edu.kit.kastel.vads.compiler.myir.node;

import java.util.Collection;

public interface Visitor<T extends Node<?>> {

    default T accumulateResults(Collection<T> results) {
        if (results.stream().findFirst().isEmpty()) {
            throw new RuntimeException("No result provided.");
        }
        return results.stream().findFirst().get();
    }
}
