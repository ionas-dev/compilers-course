package edu.kit.kastel.vads.compiler.myir.node;

import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitable;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.Collection;
import java.util.List;

public interface Node extends Visitable {

    @Override
    default <T extends Node> T accept(Visitor<T> visitor) {
        Collection<T> results =  children().stream().map(children -> children.accept(visitor)).toList();
        return visitor.accumulateResults(results);
    }

    List<? extends Node>  children();
}
