package edu.kit.kastel.vads.compiler.myir.node;

import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.Collection;
import java.util.List;

public final class ReturnNode implements Command, EndNode {

    private final PureExpressionNode expression;

    public ReturnNode(PureExpressionNode expression) {
        this.expression = expression;
    }

    public PureExpressionNode expression() {
        return expression;
    }

    @Override
    public List<PureExpressionNode> children() {
        return List.of(expression);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitReturn(this);
    }
}
