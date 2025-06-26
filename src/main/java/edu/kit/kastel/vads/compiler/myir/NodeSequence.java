package edu.kit.kastel.vads.compiler.myir;

import edu.kit.kastel.vads.compiler.myir.node.CommandNode;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

import java.util.List;
import java.util.Optional;

public record NodeSequence(List<CommandNode> commands, Optional<PureExpressionNode> pureExpressionNode) {

    public NodeSequence(PureExpressionNode pureExpressionNode) {
        this(List.of(), Optional.of(pureExpressionNode));
    }

    public NodeSequence(List<CommandNode> commands) {
        this(commands, Optional.empty());
    }

    public NodeSequence(List<CommandNode> commands, PureExpressionNode pureExpressionNode) {
        this(commands, Optional.of(pureExpressionNode));
    }

    public NodeSequence(CommandNode command) {
        this(List.of(command));
    }

    public NodeSequence() {
        this(List.of());
    }
}
