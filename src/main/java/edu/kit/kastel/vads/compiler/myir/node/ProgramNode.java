package edu.kit.kastel.vads.compiler.myir.node;

import java.util.List;

public class ProgramNode<T extends ProgramExecutionNode> implements Node {
    private final List<T> children;
    private final LabelNode start;
    private final ReturnNode end;

    public ProgramNode(List<T> children, LabelNode start, ReturnNode end) {
        this.children = children;
        this.start = start;
        this.end = end;
    }

    public LabelNode start() {
        return start;
    }

    public ReturnNode end() {
        return end;
    }

    @Override
    public List<T> children() {
        return children;
    }
}
