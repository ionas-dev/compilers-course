package edu.kit.kastel.vads.compiler.myir.node;

import java.util.ArrayList;
import java.util.List;

public final class CallAssignmentNode implements Command {


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

    @Override
    public List<PureExpressionNode> children() {
        List<PureExpressionNode> children = new ArrayList<>();
        children.add(variable);
        children.addAll(parameters);
        return children;
    }
}
