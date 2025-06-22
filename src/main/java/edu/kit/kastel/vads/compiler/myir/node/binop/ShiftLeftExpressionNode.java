package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

public class ShiftLeftExpressionNode extends BinaryExpressionNode implements PureExpressionNode {
    public ShiftLeftExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }
}
