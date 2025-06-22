package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

public final class SubtractExpressionNode extends BinaryExpressionNode implements PureExpressionNode {

    public SubtractExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }
}
