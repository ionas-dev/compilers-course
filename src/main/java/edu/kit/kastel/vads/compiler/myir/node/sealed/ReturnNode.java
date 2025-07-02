package edu.kit.kastel.vads.compiler.myir.node.sealed;

import edu.kit.kastel.vads.compiler.myir.node.CommandNode;
import edu.kit.kastel.vads.compiler.myir.node.EndNode;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.List;

public final class ReturnNode implements CommandNode, EndNode {

    private final PureExpressionNode expression;

    public ReturnNode(PureExpressionNode expression) {
        this.expression = expression;
    }

    public PureExpressionNode expression() {
        return expression;
    }

    @Override
    public List<PureExpressionNode> children() {
        return List.of(expression);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitReturn(this);
    }
}
