package edu.kit.kastel.vads.compiler.myir.node;

public interface PrimitiveNode<T extends  PrimitiveNode<?>> extends BasicBlock<T> {

    boolean sideEffect();
}
