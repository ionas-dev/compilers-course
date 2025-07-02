package edu.kit.kastel.vads.compiler.myir.node.sealed;

import edu.kit.kastel.vads.compiler.myir.node.CommandNode;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public final class CallAssignmentNode implements CommandNode {

    private final VariableNode variable;

    private final String identifier;

    private final List<PureExpressionNode> parameters;

    public CallAssignmentNode(VariableNode variableNode, String identifier, List<PureExpressionNode> parameters) {
        this.variable = variableNode;
        this.identifier = identifier;
        this.parameters = parameters;
    }

    public VariableNode variable()   {
        return variable;
    }

    public String identifier() {
        return identifier;
    }

    public List<PureExpressionNode> parameters() {
        return parameters;
    }
    @Override
    public List<PureExpressionNode> children() {
        List<PureExpressionNode> children = new ArrayList<>();
        children.add(variable);
        children.addAll(parameters);
        return children;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitCallAssignment(this);
    }
}
