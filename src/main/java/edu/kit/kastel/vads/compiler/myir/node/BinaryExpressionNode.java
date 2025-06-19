package edu.kit.kastel.vads.compiler.myir.node;

import java.util.Collection;
import java.util.List;

public abstract class BinaryExpressionNode implements PrimitiveNode<PureExpressionNode> {

    private final PureExpressionNode left;
    private final PureExpressionNode right;

    public BinaryExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        this.left = left;
        this.right = right;
    }

    public PureExpressionNode left() {
        return left;
    }

    public PureExpressionNode right() {
        return right;
    }

    @Override
    public Collection<PureExpressionNode> children() {
        return List.of(left, right);
    }

}
