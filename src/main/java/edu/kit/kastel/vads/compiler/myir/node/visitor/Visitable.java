package edu.kit.kastel.vads.compiler.myir.node.visitor;

import edu.kit.kastel.vads.compiler.myir.node.Node;

public interface Visitable {

     <T> T accept(Visitor<T> visitor);
}
