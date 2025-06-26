package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

public class NotEqualExpressionNode extends BinaryExpressionNode implements PureExpressionNode {
    public NotEqualExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitNotEqualExpression(this);
    }
}
