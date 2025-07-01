package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

public final class LessThanExpressionNode  extends BinaryExpressionNode implements PureExpressionNode {
    public LessThanExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitLessThanExpression(this);
    }
}
