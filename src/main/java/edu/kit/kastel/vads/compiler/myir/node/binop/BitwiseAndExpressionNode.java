package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

public class BitwiseAndExpressionNode extends BinaryExpressionNode implements PureExpressionNode {
    public BitwiseAndExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }
}
