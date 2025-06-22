package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

public class LessThanExpressionNode  extends BinaryExpressionNode implements PureExpressionNode {
    public LessThanExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }
}
