package edu.kit.kastel.vads.compiler.myir.node.fixed;

import edu.kit.kastel.vads.compiler.myir.node.Command;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

import java.util.Collection;
import java.util.List;

public final class Assignment implements Command<PureExpressionNode> {

    private final VariableNode variable;

    private final PureExpressionNode expression;

    public Assignment(VariableNode variable, PureExpressionNode expression) {
        this.variable = variable;
        this.expression = expression;
    }

    public VariableNode variable() {
        return variable;
    }

    public PureExpressionNode expression() {
        return expression;
    }

    @Override
    public Collection<PureExpressionNode> children() {
        return List.of(variable, expression);
    }
}
