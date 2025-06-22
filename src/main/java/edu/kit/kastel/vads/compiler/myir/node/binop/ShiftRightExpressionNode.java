package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

public class ShiftRightExpressionNode extends BinaryExpressionNode implements PureExpressionNode {
    public ShiftRightExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }
}
