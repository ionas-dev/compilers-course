package edu.kit.kastel.vads.compiler.myir;

import edu.kit.kastel.vads.compiler.myir.node.Command;
import edu.kit.kastel.vads.compiler.myir.node.Node;
import edu.kit.kastel.vads.compiler.myir.node.PrimitiveNode;
import edu.kit.kastel.vads.compiler.myir.node.VariableNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.BinaryExpressionNode;

import java.util.List;
import java.util.function.BinaryOperator;

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
}
