package edu.kit.kastel.vads.compiler.myir.node;

public interface PureExpressionNode extends PrimitiveNode {

    default boolean sideEffect() {
        return false;
    }
}
