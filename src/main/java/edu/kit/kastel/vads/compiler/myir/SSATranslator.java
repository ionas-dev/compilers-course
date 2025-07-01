package edu.kit.kastel.vads.compiler.myir;

import edu.kit.kastel.vads.compiler.myir.node.AssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.BinaryAssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.BooleanConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.CallAssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.CommandNode;
import edu.kit.kastel.vads.compiler.myir.node.IfNode;
import edu.kit.kastel.vads.compiler.myir.node.IntegerConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.JumpNode;
import edu.kit.kastel.vads.compiler.myir.node.LabelNode;
import edu.kit.kastel.vads.compiler.myir.node.Node;
import edu.kit.kastel.vads.compiler.myir.node.ProgramNode;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.ReturnNode;
import edu.kit.kastel.vads.compiler.myir.node.VariableNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.AddExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.BinaryExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.BitwiseAndExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.BitwiseOrExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.BitwiseXorExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.DivisionExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.EqualExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.GreaterThanExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.LessThanExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.ModuloExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.MultiplyExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.NotEqualExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.ShiftLeftExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.ShiftRightExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.SubtractExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.block.BasicBlock;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
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
        VariableNode assignedVariable = new VariableNode(node.variableNode().name() + "_" + assignedVariableCount + 1);

        BinaryExpressionNode expression = (BinaryExpressionNode) node.binaryExpressionNode().accept(this);

        variablesCounter.put(node.variableNode().name(), assignedVariableCount + 1);
        return new BinaryAssignmentNode(assignedVariable, expression);
    }

    @Override
    public Node visitCallAssignment(CallAssignmentNode node) {
        int assignedVariableCount = getCount(node.variable());
        VariableNode assignedVariable = new VariableNode(node.variable().name() + "_" + assignedVariableCount + 1);

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

    @Override
    public Node visitAddExpression(AddExpressionNode node) {
        return visitBinaryExpression(node);
    }

    @Override
    public Node visitSubtractExpression(SubtractExpressionNode node) {
        return visitBinaryExpression(node);
    }

    @Override
    public Node visitMultiplyExpression(MultiplyExpressionNode node) {
        return visitBinaryExpression(node);
    }

    @Override
    public Node visitDivisionExpression(DivisionExpressionNode node) {
        return visitBinaryExpression(node);
    }

    @Override
    public Node visitModuloExpression(ModuloExpressionNode node) {
        return visitBinaryExpression(node);
    }

    @Override
    public Node visitBitwiseAndExpression(BitwiseAndExpressionNode node) {
        return visitBinaryExpression(node);
    }

    @Override
    public Node visitBitwiseOrExpression(BitwiseOrExpressionNode node) {
        return visitBinaryExpression(node);
    }

    @Override
    public Node visitBitwiseXorExpression(BitwiseXorExpressionNode node) {
        return visitBinaryExpression(node);
    }

    @Override
    public Node visitEqualExpression(EqualExpressionNode node) {
        return visitBinaryExpression(node);
    }

    @Override
    public Node visitNotEqualExpression(NotEqualExpressionNode node) {
        return visitBinaryExpression(node);
    }

    @Override
    public Node visitGreaterThanExpression(GreaterThanExpressionNode node) {
        return visitBinaryExpression(node);
    }

    @Override
    public Node visitLessThanExpression(LessThanExpressionNode node) {
        return visitBinaryExpression(node);
    }

    @Override
    public Node visitShiftLeft(ShiftLeftExpressionNode node) {
        return visitBinaryExpression(node);
    }

    @Override
    public Node visitShiftRight(ShiftRightExpressionNode node) {
        return visitBinaryExpression(node);
    }

    private PureExpressionNode visitRightSidePureExpression(PureExpressionNode node) {
        if (node instanceof VariableNode variable) {
            int parameterVariableCount = getCount(variable);
            return new VariableNode(variable.name() + "_" + parameterVariableCount);
        }
        return (PureExpressionNode) node.accept(this);
    }

    private BinaryExpressionNode visitBinaryExpression(BinaryExpressionNode node) {
        PureExpressionNode leftExpression = visitRightSidePureExpression(node.left());

        PureExpressionNode rightExpression = visitRightSidePureExpression(node.right());

        try {
            return node.getClass().getConstructor(PureExpressionNode.class, PureExpressionNode.class)
                    .newInstance(leftExpression, rightExpression);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            assert false;
        }
        return null;
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
