package edu.kit.kastel.vads.compiler.myir.node;

public interface Command extends PrimitiveNode {

    @Override
    default boolean sideEffect() {
        return true;
    }
}
