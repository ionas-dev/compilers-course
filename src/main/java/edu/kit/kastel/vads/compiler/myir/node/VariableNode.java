package edu.kit.kastel.vads.compiler.myir.node;

import java.util.List;

public final class VariableNode implements PureExpressionNode {

    private final String name;

    public VariableNode(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public List<Node> children() {
        return List.of();
    }
}
