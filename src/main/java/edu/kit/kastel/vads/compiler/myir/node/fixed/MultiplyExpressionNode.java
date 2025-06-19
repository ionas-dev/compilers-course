package edu.kit.kastel.vads.compiler.myir.node.fixed;

import edu.kit.kastel.vads.compiler.myir.node.BinaryExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

public final class MultiplyExpressionNode extends BinaryExpressionNode implements PureExpressionNode {

    public MultiplyExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }
}
