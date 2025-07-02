package edu.kit.kastel.vads.compiler.myir.node.sealed;

import edu.kit.kastel.vads.compiler.myir.node.Node;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.List;

public final class VariableNode implements PureExpressionNode {

    private final String name;

    public VariableNode(String name) {
        this.name = name;
    }

    public static VariableNode temporary(int id) {
        return new VariableNode("tmp" + id);
    }

    public String name() {
        return name;
    }

    @Override
    public List<Node> children() {
        return List.of();
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitVariable(this);
    }
}
