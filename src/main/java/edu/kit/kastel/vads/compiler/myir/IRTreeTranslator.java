package edu.kit.kastel.vads.compiler.myir;

import edu.kit.kastel.vads.compiler.antlr.L2BaseVisitor;
import edu.kit.kastel.vads.compiler.antlr.L2Parser;
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
import org.antlr.v4.runtime.ParserRuleContext;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.assignmentOperator;
import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.binaryOperator;
import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.identifier;
import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.parseInt;
import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.unaryOperator;

public class IRTreeTranslator extends L2BaseVisitor<NodeSequence> {
    public IRTreeTranslator() {
    }

    @Nullable
    private ParserRuleContext currentLoopCtx = null;

    public static Collection<ProgramNode> translate(L2Parser.ProgramContext program) {
        IRTreeTranslator tree = new IRTreeTranslator();
        return program.function().stream()
                .map(tree::visitFunction)
                .map(NodeSequence::commands)
                .map(ProgramNode::new)
                .toList();
    }


    @Override
    public NodeSequence visitFunction(L2Parser.FunctionContext ctx) {
        LabelNode functionLabel = new LabelNode("_" + ctx.identifier().getText());
        List<CommandNode> commands = new ArrayList<>();
        commands.add(functionLabel);
        commands.addAll(ctx.block().statement().stream().map(this::visitStatement).map(NodeSequence::commands).flatMap(Collection::stream).toList());

        return new NodeSequence(commands);
    }

    @Override
    public NodeSequence visitParameter(L2Parser.ParameterContext ctx) {
        return new NodeSequence(new VariableNode(ctx.identifier().getText()));
    }

    @Override
    public NodeSequence visitDeclaration(L2Parser.DeclarationContext ctx) {
        if (ctx.expression() == null) {
            return new NodeSequence();
        }
        return new NodeSequence(assignmentCommands(ctx.identifier().getText(), ctx.expression()));
    }

    @Override
    public NodeSequence visitAssignment(L2Parser.AssignmentContext ctx) {
        List<CommandNode> commands = switch (assignmentOperator(ctx).getSymbol().getType()) {
            case L2Parser.ASSIGN -> assignmentCommands(identifier(ctx.leftValue()).getText(), ctx.expression());
            case L2Parser.PLUS_ASSIGN ->
                    pureBinaryAssignmentNodes(ctx, BinaryExpressionNode.BinaryExpressionType.ADDITION);
            case L2Parser.MINUS_ASSIGN ->
                    pureBinaryAssignmentNodes(ctx, BinaryExpressionNode.BinaryExpressionType.SUBTRACTION);
            case L2Parser.TIMES_ASSIGN ->
                    pureBinaryAssignmentNodes(ctx, BinaryExpressionNode.BinaryExpressionType.MULTIPLICATION);
            case L2Parser.DIV_ASSIGN -> binaryAssignmentNodes(ctx, BinaryAssignmentNode.BinaryExpressionType.DIVISION);
            case L2Parser.MOD_ASSIGN -> binaryAssignmentNodes(ctx, BinaryAssignmentNode.BinaryExpressionType.MODULO);
            case L2Parser.AND_ASSIGN ->
                    pureBinaryAssignmentNodes(ctx, BinaryExpressionNode.BinaryExpressionType.BITWISE_AND);
            case L2Parser.OR_ASSIGN ->
                    pureBinaryAssignmentNodes(ctx, BinaryExpressionNode.BinaryExpressionType.BITWISE_OR);
            case L2Parser.XOR_ASSIGN ->
                    pureBinaryAssignmentNodes(ctx, BinaryExpressionNode.BinaryExpressionType.BITWISE_XOR);
            case L2Parser.SHIFT_RIGHT ->
                    pureBinaryAssignmentNodes(ctx, BinaryExpressionNode.BinaryExpressionType.SHIFT_RIGHT);
            case L2Parser.SHIFT_LEFT ->
                    pureBinaryAssignmentNodes(ctx, BinaryExpressionNode.BinaryExpressionType.SHIFT_LEFT);
            default ->
                    throw new IllegalStateException("Unexpected value: " + assignmentOperator(ctx).getSymbol().getType());
        };
        return new NodeSequence(commands);
    }

    @Override
    public NodeSequence visitUnaryExpression(L2Parser.UnaryExpressionContext ctx) {
        NodeSequence expressionNodes = ctx.expression().accept(this);
        assert expressionNodes.pureExpressionNode().isPresent();

        return switch (unaryOperator(ctx).getSymbol().getType()) {
            case L2Parser.MINUS -> {
                BinaryExpressionNode subtractExpressionNode = new BinaryExpressionNode(new IntegerConstantNode(0), expressionNodes.pureExpressionNode().get(), BinaryExpressionNode.BinaryExpressionType.SUBTRACTION);
                yield new NodeSequence(expressionNodes.commands(), subtractExpressionNode);
            }
            case L2Parser.NOT -> {
                VariableNode temporaryVariable = VariableNode.temporary(ctx.hashCode());
                List<CommandNode> commands = conditionalCommands(expressionNodes, List.of(new AssignmentNode(temporaryVariable, new BooleanConstantNode(false))), List.of(new AssignmentNode(temporaryVariable, new BooleanConstantNode(true))), ctx);
                yield new NodeSequence(commands, temporaryVariable);
            }
            case L2Parser.BITNOT -> {
                BinaryExpressionNode bitwiseXorExpressionNode = new BinaryExpressionNode(new IntegerConstantNode(-1), expressionNodes.pureExpressionNode().get(), BinaryExpressionNode.BinaryExpressionType.BITWISE_XOR);
                yield new NodeSequence(expressionNodes.commands(), bitwiseXorExpressionNode);
            }
            default -> throw new IllegalStateException("Unexpected value: " + unaryOperator(ctx).getSymbol().getType());
        };
    }

    @Override
    public NodeSequence visitBinaryExpression(L2Parser.BinaryExpressionContext ctx) {
        return switch (binaryOperator(ctx).getSymbol().getType()) {
            case L2Parser.PLUS -> pureBinaryExpressionNodes(ctx, BinaryExpressionNode.BinaryExpressionType.ADDITION);
            case L2Parser.MINUS ->
                    pureBinaryExpressionNodes(ctx, BinaryExpressionNode.BinaryExpressionType.SUBTRACTION);
            case L2Parser.TIMES ->
                    pureBinaryExpressionNodes(ctx, BinaryExpressionNode.BinaryExpressionType.MULTIPLICATION);
            case L2Parser.DIV -> binaryExpressionNodes(ctx, BinaryAssignmentNode.BinaryExpressionType.DIVISION);
            case L2Parser.MOD -> binaryExpressionNodes(ctx, BinaryAssignmentNode.BinaryExpressionType.MODULO);
            case L2Parser.BITXOR ->
                    pureBinaryExpressionNodes(ctx, BinaryExpressionNode.BinaryExpressionType.BITWISE_XOR);
            case L2Parser.BITAND ->
                    pureBinaryExpressionNodes(ctx, BinaryExpressionNode.BinaryExpressionType.BITWISE_AND);
            case L2Parser.BITOR -> pureBinaryExpressionNodes(ctx, BinaryExpressionNode.BinaryExpressionType.BITWISE_OR);
            case L2Parser.SHIFT_LEFT ->
                    pureBinaryExpressionNodes(ctx, BinaryExpressionNode.BinaryExpressionType.SHIFT_LEFT);
            case L2Parser.SHIFT_RIGHT ->
                    pureBinaryExpressionNodes(ctx, BinaryExpressionNode.BinaryExpressionType.SHIFT_RIGHT);
            case L2Parser.EQ -> pureBinaryExpressionNodes(ctx, BinaryExpressionNode.BinaryExpressionType.EQUAL);
            case L2Parser.NEQ -> pureBinaryExpressionNodes(ctx, BinaryExpressionNode.BinaryExpressionType.NOT_EQUAL);
            case L2Parser.LT -> pureBinaryExpressionNodes(ctx, BinaryExpressionNode.BinaryExpressionType.LESS_THAN);
            case L2Parser.GT -> pureBinaryExpressionNodes(ctx, BinaryExpressionNode.BinaryExpressionType.GREATER_THAN);
            case L2Parser.GE -> pureBinaryExpressionNodes(ctx, BinaryExpressionNode.BinaryExpressionType.GREATER_EQUAL);
            case L2Parser.LE -> pureBinaryExpressionNodes(ctx, BinaryExpressionNode.BinaryExpressionType.LESS_EQUAL);
            case L2Parser.LOGOR -> logicalOrNodes(ctx.left.accept(this), ctx.right.accept(this), ctx);
            case L2Parser.LOGAND -> logicalAndNodes(ctx.left.accept(this), ctx.right.accept(this), ctx);
            default ->
                    throw new IllegalStateException("Unexpected value: " + binaryOperator(ctx).getSymbol().getType());
        };
    }

    @Override
    public NodeSequence visitIf(L2Parser.IfContext ctx) {
        NodeSequence ifNodes = ctx.expression().accept(this);
        List<CommandNode> thenCommands = ctx.ifStatement.accept(this).commands();
        List<CommandNode> elseCommands = ctx.elseStatement != null ? ctx.elseStatement.accept(this).commands() : List.of();

        return new NodeSequence(conditionalCommands(ifNodes, thenCommands, elseCommands, ctx));
    }

    @Override
    public NodeSequence visitFor(L2Parser.ForContext ctx) {
        ParserRuleContext outerLoopContext = currentLoopCtx;
        currentLoopCtx = ctx;

        String loopLabel = ".loop" + ctx.hashCode();
        String finishLabel = ".finish" + ctx.hashCode();
        String conditionLabel = ".condition" + ctx.hashCode();

        NodeSequence conditionalExpressionNodes = ctx.forExpression.accept(this);
        assert conditionalExpressionNodes.pureExpressionNode().isPresent();

        List<CommandNode> commands = new ArrayList<>();
        commands.addAll(ctx.forDeclaration.accept(this).commands());
        commands.addAll(conditionalExpressionNodes.commands());
        BinaryExpressionNode condition = new BinaryExpressionNode(conditionalExpressionNodes.pureExpressionNode().get(), new BooleanConstantNode(false), BinaryExpressionNode.BinaryExpressionType.EQUAL);
        commands.add(new IfNode(condition, new JumpNode(finishLabel)));

        commands.add(new LabelNode(loopLabel));
        commands.addAll(ctx.statement().accept(this).commands());
        commands.addAll(ctx.forAssignment.accept(this).commands());

        commands.add(new LabelNode(conditionLabel));
        commands.add(new IfNode(conditionalExpressionNodes.pureExpressionNode().get(), new JumpNode(loopLabel)));

        commands.add(new LabelNode(finishLabel));

        currentLoopCtx = outerLoopContext;
        return new NodeSequence(commands);
    }

    @Override
    public NodeSequence visitWhile(L2Parser.WhileContext ctx) {
        ParserRuleContext outerLoopContext = currentLoopCtx;
        currentLoopCtx = ctx;

        String loopLabel = ".loop" + ctx.hashCode();
        String finishLabel = ".finish" + ctx.hashCode();
        String conditionLabel = ".condition" + ctx.hashCode();

        NodeSequence conditionalExpressionNodes = ctx.expression().accept(this);
        assert conditionalExpressionNodes.pureExpressionNode().isPresent();

        List<CommandNode> commands = new ArrayList<>(conditionalExpressionNodes.commands());

        BinaryExpressionNode condition = new BinaryExpressionNode(conditionalExpressionNodes.pureExpressionNode().get(), new BooleanConstantNode(false), BinaryExpressionNode.BinaryExpressionType.EQUAL);
        commands.add(new IfNode(condition, new JumpNode(finishLabel)));

        commands.add(new LabelNode(loopLabel));
        commands.addAll(ctx.statement().accept(this).commands());

        commands.add(new LabelNode(conditionLabel));
        commands.add(new IfNode(conditionalExpressionNodes.pureExpressionNode().get(), new JumpNode(loopLabel)));

        commands.add(new LabelNode(finishLabel));

        currentLoopCtx = outerLoopContext;
        return new NodeSequence(commands);
    }


    @Override
    public NodeSequence visitPrintCall(L2Parser.PrintCallContext ctx) {
        return callNodes(ctx.PRINT().getText(), ctx.arguments());
    }

    @Override
    public NodeSequence visitReadCall(L2Parser.ReadCallContext ctx) {
        return callNodes(ctx.READ().getText(), ctx.arguments());
    }

    @Override
    public NodeSequence visitFlushCall(L2Parser.FlushCallContext ctx) {
        return callNodes(ctx.FLUSH().getText(), ctx.arguments());
    }

    @Override
    public NodeSequence visitCustomFunctionCall(L2Parser.CustomFunctionCallContext ctx) {
        return callNodes(ctx.identifier().getText(), ctx.arguments());
    }

    @Override
    public NodeSequence visitBreak(L2Parser.BreakContext ctx) {
        assert currentLoopCtx != null;
        return new NodeSequence(new JumpNode(".finish" + ctx.hashCode()));
    }

    @Override
    public NodeSequence visitContinue(L2Parser.ContinueContext ctx) {
        assert currentLoopCtx != null;
        return new NodeSequence(new JumpNode(".condition" + ctx.hashCode()));
    }

    @Override
    public NodeSequence visitReturn(L2Parser.ReturnContext ctx) {
        NodeSequence nodes = ctx.expression().accept(this);
        assert nodes.pureExpressionNode().isPresent();

        List<CommandNode> commands = new ArrayList<>(nodes.commands());
        commands.add(new ReturnNode(nodes.pureExpressionNode().get()));
        return new NodeSequence(commands);
    }

    @Override
    public NodeSequence visitTernaryExpression(L2Parser.TernaryExpressionContext ctx) {
        NodeSequence ifConditionNodes = ctx.ifCondition.accept(this);

        NodeSequence thenExpressionNodes = ctx.then.accept(this);
        assert thenExpressionNodes.pureExpressionNode().isPresent();

        NodeSequence elseExpressionNodes = ctx.else_.accept(this);
        assert elseExpressionNodes.pureExpressionNode().isPresent();

        VariableNode temporaryVariable = VariableNode.temporary(ctx.hashCode());

        thenExpressionNodes.commands().add(new AssignmentNode(temporaryVariable, thenExpressionNodes.pureExpressionNode().get()));
        elseExpressionNodes.commands().add(new AssignmentNode(temporaryVariable, elseExpressionNodes.pureExpressionNode().get()));

        List<CommandNode> commands = conditionalCommands(ifConditionNodes, thenExpressionNodes.commands(), elseExpressionNodes.commands(), ctx);
        return new NodeSequence(commands, temporaryVariable);
    }

    @Override
    public NodeSequence visitBooleanConstant(L2Parser.BooleanConstantContext ctx) {
        return new NodeSequence(new BooleanConstantNode(ctx.TRUE() != null));
    }

    @Override
    public NodeSequence visitIntConstant(L2Parser.IntConstantContext ctx) {
        return new NodeSequence(new IntegerConstantNode(parseInt(ctx)));
    }

    @Override
    public NodeSequence visitIdentiferExpression(L2Parser.IdentiferExpressionContext ctx) {
        return new NodeSequence(new VariableNode(ctx.identifier().getText()));
    }

    @Override
    protected NodeSequence aggregateResult(NodeSequence aggregate, NodeSequence nextResult) {
        List<CommandNode> commands = new ArrayList<>(aggregate.commands());
        commands.addAll(nextResult.commands());

        assert aggregate.pureExpressionNode().isEmpty() || nextResult.pureExpressionNode().isEmpty();
        Optional<PureExpressionNode> pureExpressionNode = aggregate.pureExpressionNode().isPresent()
                ? aggregate.pureExpressionNode()
                : nextResult.pureExpressionNode();

        return new NodeSequence(commands, pureExpressionNode);
    }

    @Override
    protected NodeSequence defaultResult() {
        return new NodeSequence();
    }

    private NodeSequence callNodes(String identifier, L2Parser.ArgumentsContext ctx) {
        List<CommandNode> commands = new ArrayList<>();
        List<PureExpressionNode> argumentNodes = new ArrayList<>();
        VariableNode temporaryVariable = VariableNode.temporary(ctx.hashCode());

        for (L2Parser.ExpressionContext expressionContext : ctx.expression()) {
            NodeSequence expressionNodes = expressionContext.accept(this);
            assert expressionNodes.pureExpressionNode().isPresent();

            commands.addAll(expressionNodes.commands());
            argumentNodes.add(expressionNodes.pureExpressionNode().get());
        }

        commands.add(new CallAssignmentNode(temporaryVariable, identifier, argumentNodes));
        return new NodeSequence(commands, temporaryVariable);
    }

    private NodeSequence logicalAndNodes(NodeSequence leftExpressionNodes, NodeSequence rightExpressionNodes, L2Parser.BinaryExpressionContext ctx) {
        VariableNode temporaryVariable = VariableNode.temporary(ctx.hashCode());

        List<CommandNode> commands = conditionalCommands(
                leftExpressionNodes,
                conditionalCommands(
                        rightExpressionNodes,
                        List.of(new AssignmentNode(temporaryVariable, new BooleanConstantNode(true))),
                        List.of(new AssignmentNode(temporaryVariable, new BooleanConstantNode(false))),
                        ctx.left),
                List.of(new AssignmentNode(temporaryVariable, new BooleanConstantNode(false))),
                ctx.right);

        return new NodeSequence(commands, temporaryVariable);
    }

    private NodeSequence pureBinaryExpressionNodes(L2Parser.BinaryExpressionContext ctx, BinaryExpressionNode.BinaryExpressionType type) {
        NodeSequence leftExpressionNodes = ctx.left.accept(this);
        NodeSequence rightExpressionNodes = ctx.right.accept(this);
        assert leftExpressionNodes.pureExpressionNode().isPresent() && rightExpressionNodes.pureExpressionNode().isPresent();

        List<CommandNode> commands = new ArrayList<>(leftExpressionNodes.commands());
        commands.addAll(rightExpressionNodes.commands());


        BinaryExpressionNode binaryExpressionNode = new BinaryExpressionNode(leftExpressionNodes.pureExpressionNode().get(), rightExpressionNodes.pureExpressionNode().get(), type);
        return new NodeSequence(commands, binaryExpressionNode);
    }

    private NodeSequence binaryExpressionNodes(L2Parser.BinaryExpressionContext ctx, BinaryAssignmentNode.BinaryExpressionType type) {
        NodeSequence leftExpressionNodes = ctx.left.accept(this);
        NodeSequence rightExpressionNodes = ctx.right.accept(this);
        assert leftExpressionNodes.pureExpressionNode().isPresent() && rightExpressionNodes.pureExpressionNode().isPresent();

        List<CommandNode> commands = new ArrayList<>(leftExpressionNodes.commands());
        commands.addAll(rightExpressionNodes.commands());

        VariableNode temporaryVariable = VariableNode.temporary(ctx.hashCode());
        BinaryAssignmentNode binaryAssignmentNode = new BinaryAssignmentNode(temporaryVariable, leftExpressionNodes.pureExpressionNode().get(), rightExpressionNodes.pureExpressionNode().get(), type);
        commands.add(binaryAssignmentNode);
        return new NodeSequence(commands, temporaryVariable);
    }

    private List<CommandNode> pureBinaryAssignmentNodes(L2Parser.AssignmentContext ctx, BinaryExpressionNode.BinaryExpressionType binaryExpressionType) {
        NodeSequence expressionNodes = ctx.expression().accept(this);
        assert expressionNodes.pureExpressionNode().isPresent();

        VariableNode variableNode = new VariableNode(identifier(ctx.leftValue()).getText());
        BinaryExpressionNode binaryExpressionNode = new BinaryExpressionNode(variableNode, expressionNodes.pureExpressionNode().get(), binaryExpressionType);


        List<CommandNode> commands = new ArrayList<>(expressionNodes.commands());
        commands.add(new AssignmentNode(variableNode, binaryExpressionNode));
        return commands;
    }

    private List<CommandNode> binaryAssignmentNodes(L2Parser.AssignmentContext ctx, BinaryAssignmentNode.BinaryExpressionType binaryExpressionType) {
        NodeSequence expressionNodes = ctx.expression().accept(this);
        assert expressionNodes.pureExpressionNode().isPresent();

        VariableNode variableNode = new VariableNode(identifier(ctx.leftValue()).getText());
        BinaryAssignmentNode binaryAssignmentNode = new BinaryAssignmentNode(variableNode, variableNode, expressionNodes.pureExpressionNode().get(), binaryExpressionType);


        List<CommandNode> commands = new ArrayList<>(expressionNodes.commands());
        commands.add(binaryAssignmentNode);
        return commands;
    }

    private NodeSequence logicalOrNodes(NodeSequence leftExpressionNodes, NodeSequence rightExpressionNodes, L2Parser.BinaryExpressionContext ctx) {
        assert leftExpressionNodes.pureExpressionNode().isPresent() && rightExpressionNodes.pureExpressionNode().isPresent();

        VariableNode temporaryVariable = VariableNode.temporary(ctx.hashCode());

        List<CommandNode> commands = conditionalCommands(
                leftExpressionNodes,
                List.of(new AssignmentNode(temporaryVariable, new BooleanConstantNode(true))),
                conditionalCommands(
                        rightExpressionNodes,
                        List.of(new AssignmentNode(temporaryVariable, new BooleanConstantNode(true))),
                        List.of(new AssignmentNode(temporaryVariable, new BooleanConstantNode(false))),
                        ctx),
                ctx);

        return new NodeSequence(commands, temporaryVariable);
    }

    private List<CommandNode> conditionalCommands(NodeSequence ifNodes, List<CommandNode> thenNodes, List<CommandNode> elseNodes, ParserRuleContext ctx) {
        String thenLabelValue = ".then" + ctx.hashCode();
        String finishLabelValue = ".finish" + ctx.hashCode();

        List<CommandNode> nodes = new ArrayList<>(ifNodes.commands());
        assert ifNodes.pureExpressionNode().isPresent();
        nodes.add(new IfNode(ifNodes.pureExpressionNode().get(), new JumpNode(thenLabelValue)));
        nodes.add(new LabelNode(".else" + ctx.hashCode()));

        nodes.addAll(elseNodes);
        nodes.add(new JumpNode(finishLabelValue));

        nodes.add(new LabelNode(thenLabelValue));
        nodes.addAll(thenNodes);

        nodes.add(new LabelNode(finishLabelValue));
        return nodes;
    }

    private List<CommandNode> assignmentCommands(String identifier, L2Parser.ExpressionContext ctx) {
        VariableNode variable = new VariableNode(identifier);
        NodeSequence expressionNodes = ctx.accept(this);
        assert expressionNodes.pureExpressionNode().isPresent();

        List<CommandNode> commands = new ArrayList<>(expressionNodes.commands());
        commands.add(new AssignmentNode(variable, expressionNodes.pureExpressionNode().get()));
        return commands;
    }
}
