package edu.kit.kastel.vads.compiler.myir;

import edu.kit.kastel.vads.compiler.antlr.L2BaseVisitor;
import edu.kit.kastel.vads.compiler.antlr.L2Parser;
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
import org.antlr.v4.runtime.ParserRuleContext;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
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
            case L2Parser.PLUS_ASSIGN -> binaryAssignmentNodes(ctx, AddExpressionNode.class);
            case L2Parser.MINUS_ASSIGN -> binaryAssignmentNodes(ctx, SubtractExpressionNode.class);
            case L2Parser.TIMES_ASSIGN -> binaryAssignmentNodes(ctx, MultiplyExpressionNode.class);
            case L2Parser.DIV_ASSIGN -> binaryAssignmentNodes(ctx, DivisionExpressionNode.class);
            case L2Parser.MOD_ASSIGN -> binaryAssignmentNodes(ctx, ModuloExpressionNode.class);
            case L2Parser.AND_ASSIGN -> binaryAssignmentNodes(ctx, BitwiseAndExpressionNode.class);
            case L2Parser.OR_ASSIGN -> binaryAssignmentNodes(ctx, BitwiseOrExpressionNode.class);
            case L2Parser.XOR_ASSIGN -> binaryAssignmentNodes(ctx, BitwiseXorExpressionNode.class);
            case L2Parser.SHIFT_RIGHT -> binaryAssignmentNodes(ctx, ShiftRightExpressionNode.class);
            case L2Parser.SHIFT_LEFT -> binaryAssignmentNodes(ctx, ShiftLeftExpressionNode.class);
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
                SubtractExpressionNode subtractExpressionNode = new SubtractExpressionNode(new IntegerConstantNode(0), expressionNodes.pureExpressionNode().get());
                yield new NodeSequence(expressionNodes.commands(), subtractExpressionNode);
            }
            case L2Parser.NOT -> {
                VariableNode temporaryVariable = VariableNode.temporary(ctx.hashCode());
                List<CommandNode> commands = conditionalCommands(expressionNodes, List.of(new AssignmentNode(temporaryVariable, new BooleanConstantNode(false))), List.of(new AssignmentNode(temporaryVariable, new BooleanConstantNode(true))), ctx);
                yield new NodeSequence(commands, temporaryVariable);
            }
            case L2Parser.BITNOT -> {
                BitwiseXorExpressionNode bitwiseXorExpressionNode = new BitwiseXorExpressionNode(new IntegerConstantNode(-1), expressionNodes.pureExpressionNode().get());
                yield new NodeSequence(expressionNodes.commands(), bitwiseXorExpressionNode);
            }
            default -> throw new IllegalStateException("Unexpected value: " + unaryOperator(ctx).getSymbol().getType());
        };
    }

    @Override
    public NodeSequence visitBinaryExpression(L2Parser.BinaryExpressionContext ctx) {
        return switch (binaryOperator(ctx).getSymbol().getType()) {
            case L2Parser.PLUS -> binaryExpressionNodes(ctx, AddExpressionNode.class);
            case L2Parser.MINUS -> binaryExpressionNodes(ctx, SubtractExpressionNode.class);
            case L2Parser.TIMES -> binaryExpressionNodes(ctx, MultiplyExpressionNode.class);
            case L2Parser.DIV -> binaryExpressionNodes(ctx, DivisionExpressionNode.class);
            case L2Parser.MOD -> binaryExpressionNodes(ctx, ModuloExpressionNode.class);
            case L2Parser.BITXOR -> binaryExpressionNodes(ctx, BitwiseXorExpressionNode.class);
            case L2Parser.BITAND -> binaryExpressionNodes(ctx, BitwiseAndExpressionNode.class);
            case L2Parser.BITOR -> binaryExpressionNodes(ctx, BitwiseOrExpressionNode.class);
            case L2Parser.SHIFT_LEFT -> binaryExpressionNodes(ctx, ShiftLeftExpressionNode.class);
            case L2Parser.SHIFT_RIGHT -> binaryExpressionNodes(ctx, ShiftRightExpressionNode.class);
            case L2Parser.EQ -> binaryExpressionNodes(ctx, EqualExpressionNode.class);
            case L2Parser.NEQ -> binaryExpressionNodes(ctx, NotEqualExpressionNode.class);
            case L2Parser.LT -> binaryExpressionNodes(ctx, LessThanExpressionNode.class);
            case L2Parser.GT -> binaryExpressionNodes(ctx, GreaterThanExpressionNode.class);
            case L2Parser.GE ->
                    logicalOrNodes(binaryExpressionNodes(ctx, GreaterThanExpressionNode.class), binaryExpressionNodes(ctx, EqualExpressionNode.class), ctx);
            case L2Parser.LE ->
                    logicalOrNodes(binaryExpressionNodes(ctx, LessThanExpressionNode.class), binaryExpressionNodes(ctx, EqualExpressionNode.class), ctx);
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
        commands.add(new IfNode(new EqualExpressionNode(conditionalExpressionNodes.pureExpressionNode().get(), new BooleanConstantNode(false)), new JumpNode(finishLabel)));

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

        commands.add(new IfNode(new EqualExpressionNode(conditionalExpressionNodes.pureExpressionNode().get(), new BooleanConstantNode(false)), new JumpNode(finishLabel)));

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

    private NodeSequence binaryExpressionNodes(L2Parser.BinaryExpressionContext ctx, Class<? extends BinaryExpressionNode> binaryExpression) {
        NodeSequence leftExpressionNodes = ctx.left.accept(this);
        NodeSequence rightExpressionNodes = ctx.right.accept(this);
        assert leftExpressionNodes.pureExpressionNode().isPresent() && rightExpressionNodes.pureExpressionNode().isPresent();

        List<CommandNode> commands = new ArrayList<>(leftExpressionNodes.commands());
        commands.addAll(rightExpressionNodes.commands());

        try {
            BinaryExpressionNode binaryExpressionNode = binaryExpression.getConstructor(PureExpressionNode.class, PureExpressionNode.class).newInstance(leftExpressionNodes.pureExpressionNode().get(), rightExpressionNodes.pureExpressionNode().get());
            Optional<PureExpressionNode> pureExpressionNode = Optional.empty();

            if (binaryExpressionNode instanceof PureExpressionNode) {
                pureExpressionNode = Optional.of((PureExpressionNode) binaryExpressionNode);
            } else if (binaryExpressionNode instanceof CommandNode) {
                VariableNode temporaryVariable = VariableNode.temporary(ctx.hashCode());
                commands.add(new BinaryAssignmentNode(temporaryVariable, binaryExpressionNode));
                pureExpressionNode = Optional.of(temporaryVariable);
            } else {
                assert false;
            }

            return new NodeSequence(commands, pureExpressionNode);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            assert false;
        }
        return null;
    }

    private List<CommandNode> binaryAssignmentNodes(L2Parser.AssignmentContext ctx, Class<? extends BinaryExpressionNode> BinaryExpressionNode) {
        NodeSequence expressionNodes = ctx.expression().accept(this);
        assert expressionNodes.pureExpressionNode().isPresent();

        VariableNode variableNode = new VariableNode(identifier(ctx.leftValue()).getText());
        BinaryExpressionNode binaryExpressionNode = null;
        try {
            binaryExpressionNode = BinaryExpressionNode.getConstructor(PureExpressionNode.class, PureExpressionNode.class).newInstance(variableNode, expressionNodes.pureExpressionNode().get());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            assert false;
        }

        List<CommandNode> commands = new ArrayList<>(expressionNodes.commands());
        commands.add(new BinaryAssignmentNode(variableNode, binaryExpressionNode));
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
