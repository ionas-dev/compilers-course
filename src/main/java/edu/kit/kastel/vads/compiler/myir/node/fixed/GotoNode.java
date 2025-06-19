package edu.kit.kastel.vads.compiler.myir.node.fixed;

import edu.kit.kastel.vads.compiler.myir.node.Command;
import edu.kit.kastel.vads.compiler.myir.node.PrimitiveNode;

import java.util.Collection;
import java.util.List;

public final class GotoNode implements Command<PrimitiveNode<?>> {

    private final String label;

    public GotoNode(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @Override
    public Collection<PrimitiveNode<?>> children() {
        return List.of();
    }
}
