package edu.kit.kastel.vads.compiler.antlr;

import edu.kit.kastel.vads.compiler.parser.ParseException;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Optional;
import java.util.OptionalLong;

// TODO: Introduce enums for the switches
public class ParserRuleContextUtil {

    public static Optional<TerminalNode> terminalNode(ParserRuleContext ctx) {
        return ctx.children.stream().filter(TerminalNode.class::isInstance).map(TerminalNode.class::cast).findFirst();
    }

    public static TerminalNode binaryOperator(L2Parser.BinaryExpressionContext expressionContext) {
        ParserRuleContext ctx = null;
        if (expressionContext.binaryOperatorDot() != null) {
            ctx = expressionContext.binaryOperatorDot();
        } else if (expressionContext.binaryOperatorLine() != null) {
            ctx = expressionContext.binaryOperatorLine();
        } else if (expressionContext.arithmeticShift() != null) {
            ctx = expressionContext.arithmeticShift();
        } else if (expressionContext.integerComparison() != null) {
            ctx = expressionContext.integerComparison();
        } else if (expressionContext.equality() != null) {
            ctx = expressionContext.equality();
        } else if (expressionContext.BITAND() != null) {
            return expressionContext.BITAND();
        } else if (expressionContext.BITXOR() != null) {
            return expressionContext.BITXOR();
        } else if (expressionContext.BITOR() != null) {
            return expressionContext.BITOR();
        } else if (expressionContext.LOGAND() != null) {
            return expressionContext.LOGAND();
        } else if (expressionContext.LOGOR() != null) {
            return expressionContext.LOGOR();
        }

        assert ctx != null : "Binary operator not implemented";
        Optional<TerminalNode> terminalNode = terminalNode(ctx);
        assert terminalNode.isPresent() : "Binary operator not implemented";
        return terminalNode.get();
    }

    public static TerminalNode assignmentOperator(L2Parser.AssignmentContext assignmentContext) {
        if (assignmentContext.assignOperator().ASSIGN() != null) {
            return assignmentContext.assignOperator().ASSIGN();
        } else if (assignmentContext.assignOperator().PLUS_ASSIGN() != null) {
            return assignmentContext.assignOperator().PLUS_ASSIGN();
        } else if (assignmentContext.assignOperator().MINUS_ASSIGN() != null) {
            return assignmentContext.assignOperator().MINUS_ASSIGN();
        } else if (assignmentContext.assignOperator().TIMES_ASSIGN() != null) {
            return assignmentContext.assignOperator().TIMES_ASSIGN();
        } else if (assignmentContext.assignOperator().DIV_ASSIGN() != null) {
            return assignmentContext.assignOperator().DIV_ASSIGN();
        } else if (assignmentContext.assignOperator().MOD_ASSIGN() != null) {
            return assignmentContext.assignOperator().MOD_ASSIGN();
        } else if (assignmentContext.assignOperator().AND_ASSIGN() != null) {
            return assignmentContext.assignOperator().AND_ASSIGN();
        } else if (assignmentContext.assignOperator().XOR_ASSIGN() != null) {
            return assignmentContext.assignOperator().XOR_ASSIGN();
        } else if (assignmentContext.assignOperator().OR_ASSIGN() != null) {
            return assignmentContext.assignOperator().OR_ASSIGN();
        } else if (assignmentContext.assignOperator().SHIFT_LEFT_ASSIGN() != null) {
            return assignmentContext.assignOperator().SHIFT_LEFT_ASSIGN();
        } else if (assignmentContext.assignOperator().SHIFT_RIGHT_ASSIGN() != null) {
            return assignmentContext.assignOperator().SHIFT_RIGHT_ASSIGN();
        }
        assert false : "Assignment operator not implemented";
        return null;
    }

    public static TerminalNode unaryOperator(L2Parser.UnaryExpressionContext ctx) {
        if (ctx.unaryOperator().MINUS() != null) {
            return ctx.unaryOperator().MINUS();
        } else if (ctx.unaryOperator().NOT() != null) {
            return ctx.unaryOperator().NOT();
        } else if (ctx.unaryOperator().BITNOT() != null) {
            return ctx.unaryOperator().BITNOT();
        }
        assert false : "Unary operator not implemented";
        return null;
    }

    public static TerminalNode type(L2Parser.TypeContext typeContext) {
        if (typeContext.BOOL() != null) {
            return typeContext.BOOL();
        } else if (typeContext.INT() != null) {
            return typeContext.INT();
        }

        assert false : "Assignment operator not implemented";
        return null;
    }

    public static TerminalNode identifier(L2Parser.LeftValueContext ctx) {
        if (ctx.identifier() != null) {
            return ctx.identifier().IDENT();
        } else {
            return identifier(ctx.leftValue());
        }
    }

    public static long parseInt(L2Parser.IntConstantContext ctx) {
        OptionalLong optional = OptionalLong.empty();
        if (ctx.DECNUM() != null) {
            String value = ctx.DECNUM().getText();
            optional = parseDec(value);
        } else if (ctx.HEXNUM() != null) {
            String value = ctx.HEXNUM().getText();
            optional = parseHex(value);
        }
        if (optional.isEmpty()) {
            throw new ParseException("invalid integer literal " + ctx.getText());
        }
         return optional.getAsLong();
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
