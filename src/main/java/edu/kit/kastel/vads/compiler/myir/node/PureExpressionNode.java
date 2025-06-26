package edu.kit.kastel.vads.compiler.myir.node;

import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

public interface PureExpressionNode extends PrimitiveNode {

    default boolean sideEffect() {
        return false;
    }
}
