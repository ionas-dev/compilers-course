package edu.kit.kastel.vads.compiler.myir.node.fixed;

import edu.kit.kastel.vads.compiler.myir.node.Command;
import edu.kit.kastel.vads.compiler.myir.node.PrimitiveNode;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

import java.util.Collection;
import java.util.List;

public final class IfNode implements Command<PrimitiveNode<?>> {

    private final PureExpressionNode expression;

    private final GotoNode gotoNode;

    public IfNode(PureExpressionNode expression, GotoNode gotoNode) {
        this.expression = expression;
        this.gotoNode = gotoNode;
    }

    public PureExpressionNode expression() {
        return expression;
    }

    public GotoNode gotoNode() {
        return gotoNode;
    }

    @Override
    public Collection<PrimitiveNode<?>> children() {
        return List.of(expression, gotoNode);
    }
}
