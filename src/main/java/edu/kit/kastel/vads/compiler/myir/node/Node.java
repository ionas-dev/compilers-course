package edu.kit.kastel.vads.compiler.myir.node;

import java.util.Collection;

public interface Node<K extends Node<?>> extends Visitable {

    @Override
    default <T extends Node<?>> T accept(Visitor<T> visitor) {
        Collection<T> results =  children().stream().map(children -> children.accept(visitor)).toList();
        return visitor.accumulateResults(results);
    }

    Collection<K> children();
}
