package edu.kit.kastel.vads.compiler.antlr;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public class ParserRuleContextUtil {

    public static Optional<TerminalNode> terminalNode(ParserRuleContext ctx) {
        return ctx.children.stream().filter(TerminalNode.class::isInstance).map(TerminalNode.class::cast).findFirst();
    }

    public static Optional<TerminalNode> binaryOperator(L2Parser.ExpressionContext expressionContext) {
        ParserRuleContext ctx;
        if (expressionContext.binaryOperatorDot() != null) {
            ctx = expressionContext.binaryOperatorDot();
        } else if (expressionContext.binaryOperatorLine() != null) {
            ctx = expressionContext.binaryOperatorLine();
        } else if (expressionContext.arithmeticShift() != null) {
            ctx = expressionContext.arithmeticShift();
        } else if (expressionContext.BITAND() != null) {
            return Optional.of(expressionContext.BITAND());
        } else if (expressionContext.BITOR() != null) {
            return Optional.of(expressionContext.BITOR());
        } else if (expressionContext.BITXOR() != null) {
            return Optional.of(expressionContext.BITXOR());
        } else if (expressionContext.LOGAND() != null) {
            return Optional.of(expressionContext.LOGAND());
        } else if (expressionContext.LOGOR() != null) {
            return Optional.of(expressionContext.LOGOR());
        } else {
            return Optional.empty();
        }

        return terminalNode(ctx);

    }

    public static TerminalNode identifier(L2Parser.LeftValueContext ctx) {
        if (ctx.identifier() != null) {
            return ctx.identifier().IDENT();
        } else {
            return identifier(ctx.leftValue());
        }
    }

    public static OptionalLong parseInt(L2Parser.IntConstantContext ctx) {
        OptionalLong optional = OptionalLong.empty();
        if (ctx.DECNUM() != null) {
            String value = ctx.DECNUM().getText();
            optional = parseDec(value);
        } else if (ctx.HEXNUM() != null) {
            String value = ctx.HEXNUM().getText();
            optional = parseHex(value);
        }
        return optional;
    }

    private static OptionalLong parseDec(String value) {
        long l;
        try {
            l = Long.parseLong(value, 10);
        } catch (NumberFormatException _) {
            return OptionalLong.empty();
        }
        if (l < 0 || l > Integer.toUnsignedLong(Integer.MIN_VALUE)) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(l);
    }

    private static OptionalLong parseHex(String value) {
        try {
            return OptionalLong.of(Integer.parseUnsignedInt(value, 2, value.length(),  16));
        } catch (NumberFormatException e) {
            return OptionalLong.empty();
        }
    }
}
