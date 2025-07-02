package edu.kit.kastel.vads.compiler.myir.node;

import edu.kit.kastel.vads.compiler.myir.node.sealed.BinaryExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.VariableNode;

public sealed interface PureExpressionNode extends PrimitiveNode permits ConstantNode, BinaryExpressionNode, VariableNode {

    default boolean sideEffect() {
        return false;
    }
}
