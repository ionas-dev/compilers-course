package edu.kit.kastel.vads.compiler.myir.node;

import edu.kit.kastel.vads.compiler.myir.node.binop.BinaryExpressionNode;

import java.util.Collection;
import java.util.List;

public final class AssignmentNode implements Command {

    private final VariableNode variable;

    private final PureExpressionNode expression;

    public AssignmentNode(VariableNode variable, PureExpressionNode expression) {
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
    public List<PureExpressionNode> children() {
        return List.of(variable, expression);
    }
}
