package edu.kit.kastel.vads.compiler.myir.node.block;

import edu.kit.kastel.vads.compiler.myir.node.CommandNode;
import edu.kit.kastel.vads.compiler.myir.node.EndNode;
import edu.kit.kastel.vads.compiler.myir.node.Node;
import edu.kit.kastel.vads.compiler.myir.node.StartNode;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.List;

public final class BasicBlock implements Node {

    private final StartNode start;
    private final EndNode end;
    private final List<CommandNode> commands;

    public BasicBlock(List<CommandNode> commands) {
        assert commands.getFirst() instanceof StartNode : "First command of a BasicBlock must be a StartNode";
        assert commands.getLast() instanceof EndNode : "Last command of a BasicBlock must be an EndNode";

        this.commands = commands;
        this.start = (StartNode) commands.getFirst();
        this.end = (EndNode) commands.getLast();
    }

    public StartNode start() {
        return start;
    }

    public EndNode end() {
        return end;
    }

    @Override
    public List<CommandNode> children() {
        return commands;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        // TODO: Implement method
        assert false;
        return null;
    }
}
