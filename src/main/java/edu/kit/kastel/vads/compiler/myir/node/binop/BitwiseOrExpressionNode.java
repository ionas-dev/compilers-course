package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

public class BitwiseOrExpressionNode extends BinaryExpressionNode implements PureExpressionNode {
    public BitwiseOrExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }
}
