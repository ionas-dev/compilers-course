package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.Node;
import edu.kit.kastel.vads.compiler.myir.node.PrimitiveNode;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

import java.util.List;

public abstract sealed class BinaryExpressionNode implements PrimitiveNode permits AddExpressionNode, BitwiseAndExpressionNode, BitwiseOrExpressionNode, BitwiseXorExpressionNode, DivisionExpressionNode, EqualExpressionNode, GreaterThanExpressionNode, LessThanExpressionNode, ModuloExpressionNode, MultiplyExpressionNode, NotEqualExpressionNode, ShiftLeftExpressionNode, ShiftRightExpressionNode, SubtractExpressionNode {

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
