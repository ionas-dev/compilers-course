package edu.kit.kastel.vads.compiler.myir.node.sealed;

import edu.kit.kastel.vads.compiler.myir.node.CommandNode;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.List;

public final class AssignmentNode implements CommandNode {

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

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitAssignment(this);
    }
}
