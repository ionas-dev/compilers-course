package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.antlr.L2BaseVisitor;
import edu.kit.kastel.vads.compiler.antlr.L2Parser;

import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.type;

public class ProgramAnalysis extends L2BaseVisitor<Boolean> {

    @Override
    public Boolean visitProgram(L2Parser.ProgramContext ctx) {
        boolean containsMain = ctx.function().stream().map(this::visitFunction).filter(x -> x).findAny().orElse(false);
        if (!containsMain) {
            throw new SemanticException("program doesn't contain a main function without parameters returning an int.");
        }
        return true;
    }

    @Override
    public Boolean visitFunction(L2Parser.FunctionContext ctx) {
        boolean isMain = ctx.identifier().IDENT().getText().equals("main");
        boolean hasNoParameters = ctx.parameters().parameter().isEmpty();
        boolean isInt = type(ctx.type()).getSymbol().getType() == L2Parser.INT;
        return isMain && hasNoParameters && isInt;
    }
}
