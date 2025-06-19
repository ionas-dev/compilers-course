package edu.kit.kastel.vads.compiler.myir.node.fixed;

import edu.kit.kastel.vads.compiler.myir.node.Command;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

import java.util.Collection;
import java.util.List;

public final class ReturnNode implements Command<PureExpressionNode> {

    private final PureExpressionNode expression;

    public ReturnNode(PureExpressionNode expression) {
        this.expression = expression;
    }

    public PureExpressionNode expression() {
        return expression;
    }

    @Override
    public Collection<PureExpressionNode> children() {
        return List.of(expression);
    }
}
