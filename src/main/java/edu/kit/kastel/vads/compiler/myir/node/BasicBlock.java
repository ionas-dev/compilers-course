package edu.kit.kastel.vads.compiler.myir.node;

import java.util.List;

public final class BasicBlock implements ProgramExecutionNode {

    private final StartNode start;
    private final EndNode end;
    private final List<Command> commands;

    public BasicBlock(List<Command> commands) {
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
    public List<Command> children() {
        return commands;
    }
}
