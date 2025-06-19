package edu.kit.kastel.vads.compiler.myir.node.fixed;

import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

import java.util.Collection;
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
    public Collection<PureExpressionNode> children() {
        return List.of();
    }
}
