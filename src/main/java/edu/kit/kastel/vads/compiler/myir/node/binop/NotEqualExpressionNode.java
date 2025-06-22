package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

public class NotEqualExpressionNode extends BinaryExpressionNode implements PureExpressionNode {
    public NotEqualExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }
}
