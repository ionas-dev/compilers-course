package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.antlr.L2BaseVisitor;
import edu.kit.kastel.vads.compiler.antlr.L2Parser;
import edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.binaryOperator;
import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.type;
import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.unaryOperator;

public class TypeAnalysis extends L2BaseVisitor<Integer> {

    Map<String, Integer> types = new HashMap<>();

    @Override
    public Integer visitDeclaration(L2Parser.DeclarationContext ctx) {
        String name = ctx.identifier().getText();
        Integer requiredType = type(ctx.type()).getSymbol().getType();
        if (ctx.expression() != null) {
            Integer providedType = ctx.expression().accept(this);
            if (!requiredType.equals(providedType)) {
                throw new SemanticException("types of identifier and expression wont match for: " + ctx.getText());
            }
        }
        types.put(name, requiredType);
        return requiredType;
    }

    @Override
    public Integer visitAssignment(L2Parser.AssignmentContext ctx) {
        Integer requiredType = ctx.leftValue().accept(this);
        Integer providedType = ctx.expression().accept(this);
        if (!requiredType.equals(providedType)) {
            throw new SemanticException("types of identifier and expression wont match for: " + ctx.getText());
        }
        return requiredType;
    }

    @Override
    public Integer visitParenExpression(L2Parser.ParenExpressionContext ctx) {
        return ctx.expression().accept(this);
    }

    @Override
    public Integer visitTernaryExpression(L2Parser.TernaryExpressionContext ctx) {
        Integer conditionalExpressionType = ctx.expression(0).accept(this);
        if (conditionalExpressionType != L2Parser.BOOL) {
            throw new SemanticException("ternary expression condition must be bool" + ctx.getText());
        }

        Integer typeOne = ctx.expression(1).accept(this);
        Integer typeTwo = ctx.expression(2).accept(this);
        if (!typeOne.equals(typeTwo)) {
            throw new SemanticException("types of both resulting cases wont match for: " + ctx.getText());
        }
        return typeOne;
    }

    @Override
    public Integer visitBinaryExpression(L2Parser.BinaryExpressionContext ctx) {
        Integer typeOne = ctx.expression(0).accept(this);
        Integer typeTwo = ctx.expression(1).accept(this);
        if (!typeOne.equals(typeTwo)) {
            throw new SemanticException("types of lhs and rhs in binary expression wont match for: " + ctx.getText());
        }

        TerminalNode binaryOperator = binaryOperator(ctx);
        Integer requiredType = switch (binaryOperator.getSymbol().getType()) {
            case L2Parser.PLUS, L2Parser.MINUS, L2Parser.TIMES, L2Parser.SHIFT_LEFT, L2Parser.SHIFT_RIGHT, L2Parser.LT,
                 L2Parser.LE, L2Parser.GT, L2Parser.GE, L2Parser.BITAND, L2Parser.BITOR, L2Parser.BITXOR ->
                    L2Parser.INT;
            case L2Parser.EQ, L2Parser.NEQ, L2Parser.LOGAND, L2Parser.LOGOR -> L2Parser.BOOL;
            default -> throw new SemanticException("unsupported binary operator: " + binaryOperator.getText());
        };

        if (!typeOne.equals(requiredType)) {
            throw new SemanticException("types of lhs and rhs in binary expression wont match type of binary operator for: " + ctx.getText());
        }

        return typeOne;
    }

    @Override
    public Integer visitUnaryExpression(L2Parser.UnaryExpressionContext ctx) {
        Integer type = ctx.expression().accept(this);
        // TODO: Move switches directly in util
        Integer requiredType = switch (unaryOperator(ctx).getSymbol().getType()) {
            case L2Parser.MINUS, L2Parser.BITNOT -> L2Parser.INT;
            case L2Parser.NOT -> L2Parser.BOOL;
            default -> throw new SemanticException("unsupported unary operator: " + ctx.getText());
        };

        if (!type.equals(requiredType)) {
            throw new SemanticException("types of lhs and rhs in binary expression wont match type of binary operator for: " + ctx.getText());
        }

        return type;
    }


    @Override
    public Integer visitReturn(L2Parser.ReturnContext ctx) {
        Integer providedType = ctx.expression().accept(this);
        if (providedType != L2Parser.INT) {
            throw new SemanticException("returns type of non-integer value: " + ctx.getText());
        }
        return providedType;
    }

    @Override
    public Integer visitLeftValue(L2Parser.LeftValueContext ctx) {
        if (ctx.identifier() != null) {
            return ctx.identifier().accept(this);
        }
        return ctx.leftValue().accept(this);
    }

    @Override
    public Integer visitIdentifier(L2Parser.IdentifierContext ctx) {
        return types.get(ctx.getText());
    }

    @Override
    public Integer visitIntConstant(L2Parser.IntConstantContext ctx) {
        return L2Parser.INT;
    }

    @Override
    public Integer visitBooleanConstant(L2Parser.BooleanConstantContext ctx) {
        return L2Parser.BOOL;
    }
}
