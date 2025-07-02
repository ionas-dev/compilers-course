package edu.kit.kastel.vads.compiler.myir;

import edu.kit.kastel.vads.compiler.myir.node.CommandNode;
import edu.kit.kastel.vads.compiler.myir.node.ProgramNode;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;
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

import java.util.ArrayList;
import java.util.List;

public class QuadTranslator implements Visitor<NodeSequence> {

    public static ProgramNode translate(ProgramNode program) {
        return new ProgramNode(program.accept(new QuadTranslator()).commands());
    }

    @Override
    public NodeSequence visitIntegerConstant(IntegerConstantNode node) {
        return new NodeSequence(node);
    }

    @Override
    public NodeSequence visitBooleanConstant(BooleanConstantNode node) {
        return new NodeSequence(node);
    }

    @Override
    public NodeSequence visitVariable(VariableNode node) {
        return new NodeSequence(node);
    }

    @Override
    public NodeSequence visitBinaryExpression(BinaryExpressionNode node) {
        NodeSequence leftNodes =  node.left().accept(this);
        List<CommandNode> commands = new ArrayList<>(leftNodes.commands());
        assert leftNodes.pureExpressionNode().isPresent();
        leftNodes = temporarilyAssignIfBinaryExpression(leftNodes.pureExpressionNode().get());
        commands.addAll(leftNodes.commands());

        NodeSequence rightNodes = node.right().accept(this);
        commands.addAll(rightNodes.commands());
        assert rightNodes.pureExpressionNode().isPresent();
        rightNodes = temporarilyAssignIfBinaryExpression(rightNodes.pureExpressionNode().get());
        commands.addAll(rightNodes.commands());

        BinaryExpressionNode binaryExpressionNode = new BinaryExpressionNode(leftNodes.pureExpressionNode().get(), rightNodes.pureExpressionNode().get(), node.type());
        return new NodeSequence(commands, binaryExpressionNode);
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
        NodeSequence leftNodes = node.leftExpression().accept(this);
        List<CommandNode> commands = new ArrayList<>(leftNodes.commands());
        assert leftNodes.pureExpressionNode().isPresent();
        leftNodes = temporarilyAssignIfBinaryExpression(leftNodes.pureExpressionNode().get());
        commands.addAll(leftNodes.commands());

        NodeSequence rightNodes = node.rightExpression().accept(this);
        commands.addAll(rightNodes.commands());
        assert rightNodes.pureExpressionNode().isPresent();
        rightNodes = temporarilyAssignIfBinaryExpression(rightNodes.pureExpressionNode().get());
        commands.addAll(rightNodes.commands());

        commands.add(new BinaryAssignmentNode(node.variableNode(), leftNodes.pureExpressionNode().get(), rightNodes.pureExpressionNode().get(), node.binaryExpressionType()));
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
        List<NodeSequence> nodeSequences = node.parameters().stream()
                .map(x -> x.accept(this)).toList();

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
        List<CommandNode> commands = new ArrayList<>(nodes.commands());

        assert nodes.pureExpressionNode().isPresent();
        nodes =  temporarilyAssignIfBinaryExpression(nodes.pureExpressionNode().get());
        commands.addAll(nodes.commands());

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

    private NodeSequence temporarilyAssignIfBinaryExpression(PureExpressionNode node) {
        if (node instanceof BinaryExpressionNode binaryExpressionNode) {
            VariableNode temporaryVariable = VariableNode.temporary(binaryExpressionNode.hashCode());
            AssignmentNode assignment = new AssignmentNode(temporaryVariable, binaryExpressionNode);
            return new NodeSequence(List.of(assignment), temporaryVariable);

        } else {
            return new NodeSequence(node);
        }
    }
}
