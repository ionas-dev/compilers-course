package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.Node;
import edu.kit.kastel.vads.compiler.myir.node.PrimitiveNode;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

import java.util.List;

public abstract class BinaryExpressionNode implements PrimitiveNode {

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
    public List<Node> children() {
        return List.of(left, right);
    }
}
