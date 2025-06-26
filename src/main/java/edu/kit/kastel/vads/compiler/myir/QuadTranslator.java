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
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class QuadTranslator implements Visitor<NodeSequence> {

    public static ProgramNode fromProgram(ProgramNode program) {
        ProgramNode program1 = new ProgramNode(program.accept(new QuadTranslator()).commands());
        return program1;
    }

    @Override
    public NodeSequence visitIntegerConstant(IntegerConstantNode node) {
        return visitPureExpression(node);
    }

    @Override
    public NodeSequence visitBooleanConstant(BooleanConstantNode node) {
        return visitPureExpression(node);
    }

    @Override
    public NodeSequence visitVariable(VariableNode node) {
        return visitPureExpression(node);
    }

    @Override
    public NodeSequence visitAddExpression(AddExpressionNode node) {
        return visitPureBinaryExpression(node);
    }

    @Override
    public NodeSequence visitBitwiseAndExpression(BitwiseAndExpressionNode node) {
        return visitPureBinaryExpression(node);
    }

    @Override
    public NodeSequence visitBitwiseOrExpression(BitwiseOrExpressionNode node) {
        return visitPureBinaryExpression(node);
    }

    @Override
    public NodeSequence visitBitwiseXorExpression(BitwiseXorExpressionNode node) {
        return visitPureBinaryExpression(node);
    }

    @Override
    public NodeSequence visitEqualExpression(EqualExpressionNode node) {
        return visitPureBinaryExpression(node);
    }

    @Override
    public NodeSequence visitGreaterThanExpression(GreaterThanExpressionNode node) {
        return visitPureBinaryExpression(node);
    }

    @Override
    public NodeSequence visitLessThanExpression(LessThanExpressionNode node) {
        return visitPureBinaryExpression(node);
    }

    @Override
    public NodeSequence visitMultiplyExpression(MultiplyExpressionNode node) {
        return visitPureBinaryExpression(node);
    }

    @Override
    public NodeSequence visitNotEqualExpression(NotEqualExpressionNode node) {
        return visitPureBinaryExpression(node);
    }

    @Override
    public NodeSequence visitShiftLeft(ShiftLeftExpressionNode node) {
        return visitPureBinaryExpression(node);
    }

    @Override
    public NodeSequence visitShiftRight(ShiftRightExpressionNode node) {
        return visitPureBinaryExpression(node);
    }

    @Override
    public NodeSequence visitSubtractExpression(SubtractExpressionNode node) {
        return visitPureBinaryExpression(node);
    }

    @Override
    public NodeSequence visitDivisionExpression(DivisionExpressionNode node) {
        return visitBinaryExpressionCommand(node);
    }

    @Override
    public NodeSequence visitModuloExpression(ModuloExpressionNode node) {
       return visitBinaryExpressionCommand(node);
    }

    @Override
    public NodeSequence visitJump(JumpNode node) {
        return new NodeSequence(node);
    }

    @Override
    public NodeSequence visitLabel(LabelNode node) {
        return new NodeSequence(node);
    }

    @Override
    public NodeSequence visitBinaryAssignment(BinaryAssignmentNode node) {
        NodeSequence nodes = node.binaryExpressionNode().accept(this);
        List<CommandNode> commands = new ArrayList<>(nodes.commands());
        if (nodes.pureExpressionNode().isPresent()) {
            commands.add(new AssignmentNode(node.variableNode(), nodes.pureExpressionNode().get()));
        } else {
            assert !nodes.commands().isEmpty() && nodes.commands().getLast() instanceof BinaryExpressionNode;
            commands.add(new BinaryAssignmentNode(node.variableNode(), (BinaryExpressionNode) nodes.commands().getLast()));
        }

        return new NodeSequence(commands);
    }

    @Override
    public NodeSequence visitAssignment(AssignmentNode node) {
        NodeSequence nodes = node.expression().accept(this);
        assert nodes.pureExpressionNode().isPresent();

        List<CommandNode> commands = new ArrayList<>(nodes.commands());
        if (!commands.isEmpty() && commands.getLast() instanceof AssignmentNode) {
            AssignmentNode temporaryAssignment = (AssignmentNode) commands.removeLast();
            commands.add(new AssignmentNode(node.variable(), temporaryAssignment.expression()));
        } else {
            commands.add(new AssignmentNode(node.variable(), nodes.pureExpressionNode().get()));
        }

        return new NodeSequence(commands);
    }

    @Override
    public NodeSequence visitCallAssignment(CallAssignmentNode node) {
        List<NodeSequence> nodeSequences = node.parameters().stream().map(this::visitPureExpression).toList();

        List<CommandNode> commands = new ArrayList<>();
        List<PureExpressionNode> parameters = new ArrayList<>();
        for (NodeSequence nodes : nodeSequences) {
            assert nodes.pureExpressionNode().isPresent();
            parameters.add(nodes.pureExpressionNode().get());
            commands.addAll(nodes.commands());
        }
        commands.add(new CallAssignmentNode(node.variable(), node.identifier(), parameters));

        return new NodeSequence(commands);
    }

    @Override
    public NodeSequence visitIf(IfNode node) {
        NodeSequence nodes = node.expression().accept(this);
        assert nodes.pureExpressionNode().isPresent();

        List<CommandNode> commands = new ArrayList<>(nodes.commands());
        commands.add(new IfNode(nodes.pureExpressionNode().get(), node.ifJump()));

        return new NodeSequence(commands);
    }

    @Override
    public NodeSequence visitReturn(ReturnNode node) {
        NodeSequence nodes = node.expression().accept(this);
        assert nodes.pureExpressionNode().isPresent();

        List<CommandNode> commands = new ArrayList<>(nodes.commands());
        commands.add(new ReturnNode(nodes.pureExpressionNode().get()));

        return new NodeSequence(commands);
    }

    @Override
    public NodeSequence visitProgram(ProgramNode node) {
        List<CommandNode> commands = new ArrayList<>();

        for (CommandNode command: node.children()) {
            NodeSequence nodes = command.accept(this);
            commands.addAll(nodes.commands());
        }

        return new NodeSequence(commands);
    }

    public <T extends BinaryExpressionNode & CommandNode> NodeSequence visitBinaryExpressionCommand(T node) {
        NodeSequence leftNodes = node.left().accept(this);
        assert leftNodes.pureExpressionNode().isPresent();

        NodeSequence rightNodes = node.left().accept(this);
        assert rightNodes.pureExpressionNode().isPresent();

        List<CommandNode> commands = new ArrayList<>(leftNodes.commands());
        commands.addAll(rightNodes.commands());
        try {
            commands.add((CommandNode) node.getClass()
                    .getConstructor(PureExpressionNode.class, PureExpressionNode.class)
                    .newInstance(leftNodes.pureExpressionNode().get(), rightNodes.pureExpressionNode().get()));
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            assert false;
        }

        return new NodeSequence(commands);
    }

    public <T extends BinaryExpressionNode & PureExpressionNode> NodeSequence visitPureBinaryExpression(T node) {
        PureExpressionNode pureBinaryExpressionNode = node;
        NodeSequence leftNodes =  node.left().accept(this);
        NodeSequence rightNodes = node.right().accept(this);

        List<CommandNode> commands = new ArrayList<>(leftNodes.commands());
        commands.addAll(rightNodes.commands());

        assert leftNodes.pureExpressionNode().isPresent();
        assert rightNodes.pureExpressionNode().isPresent();

        VariableNode temporaryVariableNode = VariableNode.temporary(node.hashCode());
        try {
            pureBinaryExpressionNode = (PureExpressionNode) node.getClass()
                    .getConstructor(PureExpressionNode.class, PureExpressionNode.class)
                    .newInstance(leftNodes.pureExpressionNode().get(), rightNodes.pureExpressionNode().get());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            assert false;
        }
        AssignmentNode assignmentNode = new AssignmentNode(temporaryVariableNode, pureBinaryExpressionNode);
        commands.add(assignmentNode);
        return new NodeSequence(commands, temporaryVariableNode);
    }

    public NodeSequence visitPureExpression(PureExpressionNode node) {
        if (node instanceof BinaryExpressionNode) {
            return node.accept(this);
        }
        return new NodeSequence(node);
    }
}
