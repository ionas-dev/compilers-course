package edu.kit.kastel.vads.compiler.antlr;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Optional;
import java.util.OptionalLong;

public class ParserRuleContextUtil {

    public static Optional<TerminalNode> terminalNode(ParserRuleContext ctx) {
        return ctx.children.stream().filter(TerminalNode.class::isInstance).map(TerminalNode.class::cast).findFirst();
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
            String value = ctx.DECNUM().getText();
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
