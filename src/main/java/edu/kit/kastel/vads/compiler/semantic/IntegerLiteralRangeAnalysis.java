package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.antlr.L2BaseListener;
import edu.kit.kastel.vads.compiler.antlr.L2Parser;

import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.parseInt;

public class IntegerLiteralRangeAnalysis extends L2BaseListener {

    @Override
    public void enterIntConstant(L2Parser.IntConstantContext ctx) {
        try { parseInt(ctx); }
        catch (Exception e) {
            throw new SemanticException("invalid integer literal " + ctx.getText());
        }
    }
}
