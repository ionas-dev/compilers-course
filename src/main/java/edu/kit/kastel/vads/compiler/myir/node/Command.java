package edu.kit.kastel.vads.compiler.myir.node;

public interface Command<T extends PrimitiveNode<?>> extends PrimitiveNode<T> {

    @Override
    default boolean sideEffect() {
        return true;
    }
}
