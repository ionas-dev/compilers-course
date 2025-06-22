package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

public final class AddExpressionNode extends BinaryExpressionNode implements PureExpressionNode {

    public AddExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }
}
