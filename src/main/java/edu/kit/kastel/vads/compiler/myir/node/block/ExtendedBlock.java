package edu.kit.kastel.vads.compiler.myir.node.block;

import edu.kit.kastel.vads.compiler.myir.node.Node;
import edu.kit.kastel.vads.compiler.myir.node.StartNode;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.List;

public final class ExtendedBlock implements Node {

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

    @Override
    public <T> T accept(Visitor<T> visitor) {
        // TODO: Implement method
        assert false;
        return null;
    }
}
