package edu.kit.kastel.vads.compiler.myir.node;

public interface Visitable {

     <T extends Node<?>> T accept(Visitor<T> visitor);
}
