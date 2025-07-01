package edu.kit.kastel.vads.compiler.myir.node;

import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.List;

public class ProgramNode implements Node {
    private final List<CommandNode> children;

    public ProgramNode(List<CommandNode> children) {
        this.children = children;
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
