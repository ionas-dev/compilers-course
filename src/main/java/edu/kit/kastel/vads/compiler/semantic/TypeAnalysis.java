package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.antlr.L2BaseVisitor;
import edu.kit.kastel.vads.compiler.antlr.L2Parser;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.binaryOperator;
import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.type;
import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.unaryOperator;

public class TypeAnalysis extends L2BaseVisitor<OptionalInt> {

    private record FunctionType(List<Integer> parameterTypes, int returnType) {}

    private final Map<String, FunctionType> functionTypes = new HashMap<>();
    private String currentFunctionName = "";

    private final Map<String, Integer> functionVariableTypes = new HashMap<>();

    @Override
    public OptionalInt visitProgram(L2Parser.ProgramContext ctx) {
        for (L2Parser.FunctionContext functionContext : ctx.function()) {
            currentFunctionName = functionContext.identifier().IDENT().getText();
            functionVariableTypes.clear();

            int returnType = type(functionContext.type()).getSymbol().getType();

            List<Integer> parameterTypes = new ArrayList<>();
            for (L2Parser.ParameterContext parameterContext: functionContext.parameters().parameter()) {
                int parameterType = type(parameterContext.type()).getSymbol().getType();
                functionVariableTypes.put(parameterContext.identifier().IDENT().getText(), parameterType);
                parameterTypes.add(parameterType);
            }

            functionTypes.put(currentFunctionName, new FunctionType(parameterTypes, returnType));
        }

        return super.visitProgram(ctx);
    }

    @Override
    public OptionalInt visitFunction(L2Parser.FunctionContext ctx) {
        currentFunctionName = ctx.identifier().IDENT().getText();
        return visitBlock(ctx.block());
    }

    @Override
    public OptionalInt visitDeclaration(L2Parser.DeclarationContext ctx) {
        String name = ctx.identifier().getText();
        int requiredType = type(ctx.type()).getSymbol().getType();
        if (ctx.expression() != null) {
            OptionalInt providedType = ctx.expression().accept(this);
            if (!OptionalInt.of(requiredType).equals(providedType)) {
                throw new SemanticException("types of identifier and expression wont match for: " + ctx.getText());
            }
        }
        functionVariableTypes.put(name, requiredType);
        return OptionalInt.empty();
    }

    @Override
    public OptionalInt visitAssignment(L2Parser.AssignmentContext ctx) {
        OptionalInt requiredType = ctx.leftValue().accept(this);
        OptionalInt providedType = ctx.expression().accept(this);
        if (!requiredType.equals(providedType)) {
            throw new SemanticException("types of identifier and expression wont match for: " + ctx.getText());
        }
        return OptionalInt.empty();
    }

    @Override
    public OptionalInt visitPrintCall(L2Parser.PrintCallContext ctx) {
        List<L2Parser.ExpressionContext> argumentExpressions = ctx.arguments().expression();
        if (argumentExpressions.size() != 1) {
            throw new SemanticException("expected 1 argument, got " + argumentExpressions.size() + ":" + ctx.getText());
        } else if (!argumentExpressions.getFirst().accept(this).equals(OptionalInt.of(L2Parser.INT))) {
            throw new SemanticException("expected integer argument, got another type:" + ctx.getText());
        }
        return OptionalInt.of(L2Parser.INT);
    }

    @Override
    public OptionalInt visitReadCall(L2Parser.ReadCallContext ctx) {
        List<L2Parser.ExpressionContext> argumentExpressions = ctx.arguments().expression();
        if (!argumentExpressions.isEmpty()) {
            throw new SemanticException("expected 0 arguments, got " + argumentExpressions.size() + ":" + ctx.getText());
        }
        return OptionalInt.of(L2Parser.INT);
    }

    @Override
    public OptionalInt visitFlushCall(L2Parser.FlushCallContext ctx) {
        List<L2Parser.ExpressionContext> argumentExpressions = ctx.arguments().expression();
        if (!argumentExpressions.isEmpty()) {
            throw new SemanticException("expected 0 arguments, got " + argumentExpressions.size() + ":" + ctx.getText());
        }
        return OptionalInt.of(L2Parser.INT);
    }

    @Override
    public OptionalInt visitCustomFunctionCall(L2Parser.CustomFunctionCallContext ctx) {
        String functionName = ctx.identifier().IDENT().getText();
        List<L2Parser.ExpressionContext> argumentExpressions = ctx.arguments().expression();

        if (!functionTypes.containsKey(functionName)) {
            throw new SemanticException("unknown function: " + functionName);
        }
        List<Integer> parameterTypes = functionTypes.get(functionName).parameterTypes;

        if (argumentExpressions.size() != parameterTypes.size()) {
            throw new SemanticException("expected " + parameterTypes.size()  + " argument, got " + argumentExpressions.size() + ":" + ctx.getText());
        }

        for (int i = 0; i < argumentExpressions.size(); i++) {
            if (!OptionalInt.of(parameterTypes.get(i)).equals(argumentExpressions.get(i).accept(this))) {
                throw new SemanticException("expected another type for argument " + i + ", in: " + ctx.getText());
            }
        }

        return OptionalInt.of(functionTypes.get(functionName).returnType);
    }

    @Override
    public OptionalInt visitParenExpression(L2Parser.ParenExpressionContext ctx) {
        return ctx.expression().accept(this);
    }

    @Override
    public OptionalInt visitTernaryExpression(L2Parser.TernaryExpressionContext ctx) {
        OptionalInt conditionalExpressionType = ctx.expression(0).accept(this);
        if (!OptionalInt.of(L2Parser.BOOL).equals(conditionalExpressionType)) {
            throw new SemanticException("ternary expression condition must be bool" + ctx.getText());
        }

        OptionalInt typeOne = ctx.expression(1).accept(this);
        OptionalInt typeTwo = ctx.expression(2).accept(this);
        if (!typeOne.equals(typeTwo)) {
            throw new SemanticException("types of both resulting cases wont match for: " + ctx.getText());
        }
        return typeOne;
    }

    @Override
    public OptionalInt visitBinaryExpression(L2Parser.BinaryExpressionContext ctx) {
        OptionalInt typeOne = ctx.expression(0).accept(this);
        OptionalInt typeTwo = ctx.expression(1).accept(this);
        if (!typeOne.equals(typeTwo)) {
            throw new SemanticException("types of lhs and rhs in binary expression wont match for: " + ctx.getText());
        }

        TerminalNode binaryOperator = binaryOperator(ctx);
        Integer requiredType = switch (binaryOperator.getSymbol().getType()) {
            case L2Parser.PLUS, L2Parser.MINUS, L2Parser.TIMES, L2Parser.DIV, L2Parser.MOD, L2Parser.SHIFT_LEFT, L2Parser.SHIFT_RIGHT, L2Parser.LT,
                 L2Parser.LE, L2Parser.GT, L2Parser.GE, L2Parser.BITAND, L2Parser.BITOR, L2Parser.BITXOR ->
                    L2Parser.INT;
            case L2Parser.EQ, L2Parser.NEQ, L2Parser.LOGAND, L2Parser.LOGOR -> L2Parser.BOOL;
            default -> throw new SemanticException("unsupported binary operator: " + binaryOperator.getText());
        };

        if (!OptionalInt.of(requiredType).equals(typeOne)) {
            throw new SemanticException("types of lhs and rhs in binary expression wont match type of binary operator for: " + ctx.getText());
        }

        return typeOne;
    }

    @Override
    public OptionalInt visitUnaryExpression(L2Parser.UnaryExpressionContext ctx) {
        OptionalInt type = ctx.expression().accept(this);
        // TODO: Move switches directly in util
        Integer requiredType = switch (unaryOperator(ctx).getSymbol().getType()) {
            case L2Parser.MINUS, L2Parser.BITNOT -> L2Parser.INT;
            case L2Parser.NOT -> L2Parser.BOOL;
            default -> throw new SemanticException("unsupported unary operator: " + ctx.getText());
        };

        if (!OptionalInt.of(requiredType).equals(type)) {
            throw new SemanticException("types of lhs and rhs in binary expression wont match type of binary operator for: " + ctx.getText());
        }

        return type;
    }


    @Override
    public OptionalInt visitReturn(L2Parser.ReturnContext ctx) {
        OptionalInt providedType = ctx.expression().accept(this);
        int functionReturnType = functionTypes.get(currentFunctionName).returnType;
        if (!OptionalInt.of(functionReturnType).equals(providedType)) {
            throw new SemanticException("return type does not match function return type in function: " + currentFunctionName);
        }
        return OptionalInt.empty();
    }

    @Override
    public OptionalInt visitLeftValue(L2Parser.LeftValueContext ctx) {
        if (ctx.identifier() != null) {
            return ctx.identifier().accept(this);
        }
        return ctx.leftValue().accept(this);
    }

    @Override
    public OptionalInt visitIdentifier(L2Parser.IdentifierContext ctx) {
        return OptionalInt.of(functionVariableTypes.get(ctx.getText()));
    }

    @Override
    public OptionalInt visitIntConstant(L2Parser.IntConstantContext ctx) {
        return OptionalInt.of(L2Parser.INT);
    }

    @Override
    public OptionalInt visitBooleanConstant(L2Parser.BooleanConstantContext ctx) {
        return OptionalInt.of(L2Parser.BOOL);
    }
}
