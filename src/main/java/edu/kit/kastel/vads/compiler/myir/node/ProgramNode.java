package edu.kit.kastel.vads.compiler.myir.node;

import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.List;

public class ProgramNode implements Node {
    private final List<CommandNode> children;
    private final LabelNode start;
    private final ReturnNode end;

    public ProgramNode(List<CommandNode> children) {
        this.children = children;
        assert !children.isEmpty() && children.getFirst() instanceof LabelNode;
        assert !children.isEmpty() && children.getLast() instanceof ReturnNode;

        this.start = (LabelNode) children.getFirst();
        this.end = (ReturnNode) children.getLast();
    }

    public LabelNode start() {
        return start;
    }

    public ReturnNode end() {
        return end;
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
