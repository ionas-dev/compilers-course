package edu.kit.kastel.vads.compiler.myir.node.sealed;

import edu.kit.kastel.vads.compiler.myir.node.CommandNode;
import edu.kit.kastel.vads.compiler.myir.node.EndNode;
import edu.kit.kastel.vads.compiler.myir.node.Node;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.List;

public final class JumpNode implements CommandNode, EndNode {

    private final String label;

    public JumpNode(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @Override
    public List<? extends Node> children() {
        return List.of();
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitJump(this);
    }
}
