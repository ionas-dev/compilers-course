package edu.kit.kastel.vads.compiler.myir.node.sealed;

import edu.kit.kastel.vads.compiler.myir.node.CommandNode;
import edu.kit.kastel.vads.compiler.myir.node.PrimitiveNode;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.List;

public final class BinaryAssignmentNode implements CommandNode {

    private final VariableNode variableNode;
    private final BinaryExpressionType type;
    private final PureExpressionNode leftExpression;
    private final PureExpressionNode rightExpression;


    public BinaryAssignmentNode(VariableNode variableNode, PureExpressionNode leftExpression, PureExpressionNode rightExpression, BinaryExpressionType type) {
        this.variableNode = variableNode;
        this.type = type;
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    public VariableNode variableNode() {
        return variableNode;
    }

    public PureExpressionNode leftExpression() {
        return leftExpression;
    }

    public PureExpressionNode rightExpression() {
        return rightExpression;
    }

    public BinaryExpressionType binaryExpressionType() {
        return type;
    }

    @Override
    public List<PrimitiveNode> children() {
        return List.of(variableNode, leftExpression, rightExpression);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitBinaryAssignment(this);
    }

    public enum BinaryExpressionType {
        DIVISION,
        MODULO
    }
}
