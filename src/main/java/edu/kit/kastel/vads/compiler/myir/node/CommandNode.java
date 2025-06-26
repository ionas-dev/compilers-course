package edu.kit.kastel.vads.compiler.myir.node;

public interface CommandNode extends PrimitiveNode {

    @Override
    default boolean sideEffect() {
        return true;
    }
}
