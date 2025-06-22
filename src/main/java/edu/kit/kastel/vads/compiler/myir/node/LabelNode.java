package edu.kit.kastel.vads.compiler.myir.node;

import java.util.Collection;
import java.util.List;

public final class LabelNode implements Command, StartNode {

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
}
