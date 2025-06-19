package edu.kit.kastel.vads.compiler.myir.node.fixed;

import edu.kit.kastel.vads.compiler.myir.node.Command;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

import java.util.Collection;

public final class CallNode implements Command<PureExpressionNode> {

    private final Collection<PureExpressionNode> parameters;

    public CallNode(Collection<PureExpressionNode> parameters) {
        this.parameters = parameters;
    }

    @Override
    public Collection<PureExpressionNode> children() {
        return parameters.stream().toList();
    }
}
