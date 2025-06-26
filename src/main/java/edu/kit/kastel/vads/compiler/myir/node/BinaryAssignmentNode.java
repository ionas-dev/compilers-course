package edu.kit.kastel.vads.compiler.myir.node;

import edu.kit.kastel.vads.compiler.myir.node.binop.BinaryExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.List;

public class BinaryAssignmentNode implements Command {

    private final VariableNode variableNode;

    private final BinaryExpressionNode binaryExpressionNode;

    public BinaryAssignmentNode(VariableNode variableNode, BinaryExpressionNode binaryExpressionNode) {
        this.variableNode = variableNode;
        this.binaryExpressionNode = binaryExpressionNode;
    }

    public VariableNode variableNode() {
        return variableNode;
    }

    public BinaryExpressionNode binaryExpressionNode() {
        return binaryExpressionNode;
    }

    @Override
    public List<PrimitiveNode> children() {
        return List.of(variableNode, binaryExpressionNode);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitBinaryAssignment(this);
    }
}
