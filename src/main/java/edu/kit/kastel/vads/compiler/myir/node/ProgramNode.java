package edu.kit.kastel.vads.compiler.myir.node;

import edu.kit.kastel.vads.compiler.myir.node.sealed.LabelNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.ReturnNode;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.List;

public final class ProgramNode implements Node {
    private final List<CommandNode> children;
    private final LabelNode start;

    public ProgramNode(List<CommandNode> children) {
        this.children = children;
        assert !children.isEmpty() && children.getFirst() instanceof LabelNode;

        this.start = (LabelNode) children.getFirst();
    }

    public LabelNode start() {
        return start;
    }

    @Override
    public List<CommandNode> children() {
        return children;
    }

    @Override
    public <K> K accept(Visitor<K> visitor) {
        return visitor.visitProgram(this);
    }
}
