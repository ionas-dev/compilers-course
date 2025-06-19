package edu.kit.kastel.vads.compiler.myir.node.fixed;

import edu.kit.kastel.vads.compiler.myir.node.Command;
import edu.kit.kastel.vads.compiler.myir.node.PrimitiveNode;

import java.util.Collection;
import java.util.List;

public final class LabelNode implements Command<PrimitiveNode<?>> {

    private final String value;

    public LabelNode(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public Collection<PrimitiveNode<?>> children() {
        return List.of();
    }
}
