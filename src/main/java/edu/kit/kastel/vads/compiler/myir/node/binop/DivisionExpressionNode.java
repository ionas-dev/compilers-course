package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.CommandNode;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

public final class DivisionExpressionNode extends BinaryExpressionNode implements CommandNode {

    public DivisionExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitDivisionExpression(this);
    }
}
