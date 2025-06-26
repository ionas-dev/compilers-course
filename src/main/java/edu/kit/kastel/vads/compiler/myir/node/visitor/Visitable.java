package edu.kit.kastel.vads.compiler.myir.node.visitor;

public interface Visitable {

     <T> T accept(Visitor<T> visitor);
}
