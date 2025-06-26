package edu.kit.kastel.vads.compiler.myir.node;

import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

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

    public JumpNode ifJump() {
        return ifJump;
    }


    @Override
    public List<PrimitiveNode> children() {
        return List.of(expression, ifJump);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitIf(this);
    }
}
