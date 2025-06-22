package edu.kit.kastel.vads.compiler.myir.node;

import java.util.Collection;
import java.util.List;

public final class JumpNode implements Command, EndNode {

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
}
