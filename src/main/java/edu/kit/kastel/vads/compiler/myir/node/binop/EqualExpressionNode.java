package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

public class EqualExpressionNode extends BinaryExpressionNode implements PureExpressionNode {
    public EqualExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }
}
