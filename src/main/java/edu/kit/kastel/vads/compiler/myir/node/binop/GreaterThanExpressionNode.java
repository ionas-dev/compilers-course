package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

public class GreaterThanExpressionNode  extends BinaryExpressionNode implements PureExpressionNode {
    public GreaterThanExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }
}
