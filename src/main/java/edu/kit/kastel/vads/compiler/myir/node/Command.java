package edu.kit.kastel.vads.compiler.myir.node;

public interface Command extends PrimitiveNode, ProgramExecutionNode {

    @Override
    default boolean sideEffect() {
        return true;
    }
}
