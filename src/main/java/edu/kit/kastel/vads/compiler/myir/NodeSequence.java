package edu.kit.kastel.vads.compiler.myir;

import edu.kit.kastel.vads.compiler.myir.node.Command;
import edu.kit.kastel.vads.compiler.myir.node.Node;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

import java.util.List;
import java.util.Optional;

public record NodeSequence(List<Command> commands, Optional<PureExpressionNode> pureExpressionNode) {

    public NodeSequence(PureExpressionNode pureExpressionNode) {
        this(List.of(), Optional.of(pureExpressionNode));
    }

    public NodeSequence(List<Command> commands) {
        this(commands, Optional.empty());
    }

    public NodeSequence(List<Command> commands, PureExpressionNode pureExpressionNode) {
        this(commands, Optional.of(pureExpressionNode));
    }

    public NodeSequence(Command command) {
        this(List.of(command));
    }

    public NodeSequence() {
        this(List.of());
    }
}
