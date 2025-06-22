package edu.kit.kastel.vads.compiler.myir.node;

import java.util.Collection;
import java.util.List;

public final class IfNode implements Command {

    private final PureExpressionNode expression;
    private final JumpNode ifJump;

    public IfNode(PureExpressionNode expression, JumpNode ifJump) {
        this.expression = expression;
        this.ifJump = ifJump;
    }

    public PureExpressionNode expression() {
        return expression;
    }

    public JumpNode IfJump() {
        return ifJump;
    }


    @Override
    public List<PrimitiveNode> children() {
        return List.of(expression, ifJump);
    }
}
