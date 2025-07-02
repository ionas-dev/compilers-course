package edu.kit.kastel.vads.compiler.myir;

import edu.kit.kastel.vads.compiler.myir.node.CommandNode;
import edu.kit.kastel.vads.compiler.myir.node.Node;
import edu.kit.kastel.vads.compiler.myir.node.ProgramNode;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.block.BasicBlock;
import edu.kit.kastel.vads.compiler.myir.node.sealed.AssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.BinaryAssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.BinaryExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.BooleanConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.CallAssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.IfNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.IntegerConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.JumpNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.LabelNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.ReturnNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.VariableNode;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SSATranslator implements Visitor<Node> {

    private final Map<String, Integer> variablesCounter = new HashMap<>();
    private final Map<BasicBlock, Map<String, String>> phiMapping = new HashMap<>();

    @Nullable
    private BasicBlock visitingBlock = null;

    public static BasicBlock translate(BasicBlock block) {
        return (BasicBlock) block.accept(new SSATranslator());
    }

    public static ProgramNode translate(ProgramNode program) {
        return new ProgramNode(program.accept(new BasicBlockTranslator()).stream()
                .map(new SSATranslator()::visitBasicBlock)
                .map(BasicBlock::children)
                .flatMap(Collection::stream)
                .toList());
    }

    @Override
    public BasicBlock visitBasicBlock(BasicBlock node) {
        visitingBlock = node;

        return new BasicBlock(node.children().stream()
                .map(this::visitNode)
                .filter(CommandNode.class::isInstance)
                .map(CommandNode.class::cast)
                .toList());
    }

    @Override
    public Node visitAssignment(AssignmentNode node) {
        int assignedVariableCount = getCount(node.variable());
        VariableNode assignedVariable = new VariableNode(node.variable().name() + "_" + (assignedVariableCount + 1));

        PureExpressionNode expression = visitRightSidePureExpression(node.expression());

        variablesCounter.put(node.variable().name(), assignedVariableCount + 1);
        return new AssignmentNode(assignedVariable, expression);
    }

    @Override
    public Node visitBinaryAssignment(BinaryAssignmentNode node) {
        int assignedVariableCount = getCount(node.variableNode());
        VariableNode assignedVariable = new VariableNode(node.variableNode().name() + "_" + (assignedVariableCount + 1));

        PureExpressionNode leftExpression = visitRightSidePureExpression(node.leftExpression());
        PureExpressionNode rightExpression = visitRightSidePureExpression(node.rightExpression());


        variablesCounter.put(node.variableNode().name(), assignedVariableCount + 1);
        return new BinaryAssignmentNode(assignedVariable, leftExpression, rightExpression, node.binaryExpressionType());
    }

    @Override
    public Node visitCallAssignment(CallAssignmentNode node) {
        int assignedVariableCount = getCount(node.variable());
        VariableNode assignedVariable = new VariableNode(node.variable().name() + "_" + (assignedVariableCount + 1));

        List<PureExpressionNode> parameters = node.parameters().stream()
                .map(this::visitRightSidePureExpression)
                .toList();

        variablesCounter.put(node.variable().name(), assignedVariableCount + 1);
        return new CallAssignmentNode(assignedVariable, node.identifier(), parameters);
    }

    @Override
    public Node visitIf(IfNode node) {
        PureExpressionNode expression = visitRightSidePureExpression(node.expression());
        return new IfNode(expression, node.ifJump());
    }

    @Override
    public Node visitJump(JumpNode node) {
        // TODO: Implement
        //        phiMapping.get(visitingBlock).put(node.label(). )

        return node;
    }

    @Override
    public Node visitIntegerConstant(IntegerConstantNode node) {
        return node;
    }

    @Override
    public Node visitLabel(LabelNode node) {
        return node;
    }

    @Override
    public Node visitVariable(VariableNode node) {
        assert false;
        return node;
    }

    @Override
    public Node visitReturn(ReturnNode node) {
        PureExpressionNode expression = visitRightSidePureExpression(node.expression());
        return new ReturnNode(expression);
    }

    @Override
    public Node visitBooleanConstant(BooleanConstantNode node) {
        return node;
    }

    private PureExpressionNode visitRightSidePureExpression(PureExpressionNode node) {
        if (node instanceof VariableNode variable) {
            int parameterVariableCount = getCount(variable);
            return new VariableNode(variable.name() + "_" + parameterVariableCount);
        }
        return (PureExpressionNode) node.accept(this);
    }

    @Override
    public BinaryExpressionNode visitBinaryExpression(BinaryExpressionNode node) {
        PureExpressionNode leftExpression = visitRightSidePureExpression(node.left());
        PureExpressionNode rightExpression = visitRightSidePureExpression(node.right());

        return new BinaryExpressionNode(leftExpression, rightExpression, node.type());
    }

    private int getCount(VariableNode variable) {
        if (variablesCounter.containsKey(variable.name())) {
            return variablesCounter.get(variable.name());
        } else {
            variablesCounter.put(variable.name(), 0);
            return 0;
        }
    }
}
