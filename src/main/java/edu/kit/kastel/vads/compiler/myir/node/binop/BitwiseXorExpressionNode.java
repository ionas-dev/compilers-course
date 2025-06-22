package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

public class BitwiseXorExpressionNode extends BinaryExpressionNode implements PureExpressionNode {
    public BitwiseXorExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }
}
