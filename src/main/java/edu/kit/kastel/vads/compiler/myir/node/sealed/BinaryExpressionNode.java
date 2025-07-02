package edu.kit.kastel.vads.compiler.myir.node.sealed;

import edu.kit.kastel.vads.compiler.myir.node.Node;
import edu.kit.kastel.vads.compiler.myir.node.PrimitiveNode;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.List;

public final class BinaryExpressionNode implements PrimitiveNode, PureExpressionNode {

    private final PureExpressionNode left;
    private final PureExpressionNode right;
    private final BinaryExpressionType type;

    public BinaryExpressionNode(PureExpressionNode left, PureExpressionNode right, BinaryExpressionType type) {
        this.left = left;
        this.right = right;
        this.type = type;
    }

    public PureExpressionNode left() {
        return left;
    }

    public PureExpressionNode right() {
        return right;
    }

    public BinaryExpressionType type() {
        return type;
    }

    @Override
    public List<Node> children() {
        return List.of(left, right);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitBinaryExpression(this);
    }

    public enum BinaryExpressionType {
        ADDITION,
        SUBTRACTION,
        MULTIPLICATION,
        BITWISE_AND,
        BITWISE_OR,
        BITWISE_XOR,
        SHIFT_LEFT,
        SHIFT_RIGHT,
        GREATER_THAN,
        LESS_THAN,
        GREATER_EQUAL,
        LESS_EQUAL,
        EQUAL,
        NOT_EQUAL,
    }
}
