package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.antlr.L2BaseListener;
import edu.kit.kastel.vads.compiler.antlr.L2BaseVisitor;
import edu.kit.kastel.vads.compiler.antlr.L2Parser;
import edu.kit.kastel.vads.compiler.parser.ast.LiteralTree;
import edu.kit.kastel.vads.compiler.parser.visitor.NoOpVisitor;
import edu.kit.kastel.vads.compiler.parser.visitor.Unit;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.OptionalLong;

import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.parseInt;

public class IntegerLiteralRangeAnalysis extends L2BaseListener {

    @Override
    public void enterIntConstant(L2Parser.IntConstantContext ctx) {
        if (parseInt(ctx).isEmpty())
            throw new SemanticException("invalid integer literal " + ctx.getText());
    }
}
