package edu.kit.kastel.vads.compiler.myir.node;

import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.List;

public final class LabelNode implements CommandNode, StartNode {

    private final String value;

    public LabelNode(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public List<? extends Node> children() {
        return List.of();
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitLabel(this);
    }
}
