package edu.kit.kastel.vads.compiler.myir.node.sealed;

import edu.kit.kastel.vads.compiler.myir.node.CommandNode;
import edu.kit.kastel.vads.compiler.myir.node.Node;
import edu.kit.kastel.vads.compiler.myir.node.StartNode;
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
