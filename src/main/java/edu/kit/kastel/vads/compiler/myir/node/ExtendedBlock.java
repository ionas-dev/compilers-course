package edu.kit.kastel.vads.compiler.myir.node;

import java.util.Collection;
import java.util.List;

public final class ExtendedBlock implements ProgramExecutionNode {

    private final StartNode start;
    private final List<BasicBlock> blocks;

    public ExtendedBlock(List<BasicBlock> blocks, StartNode start) {
        this.blocks = blocks;
        this.start = start;
    }

    public StartNode start() {
        return start;
    }

    @Override
    public List<BasicBlock> children() {
        return blocks;
    }
}
