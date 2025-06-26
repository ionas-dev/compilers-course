package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

public class EqualExpressionNode extends BinaryExpressionNode implements PureExpressionNode {
    public EqualExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitEqualExpression(this);
    }
}
