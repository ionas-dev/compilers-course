package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.antlr.L2BaseListener;
import edu.kit.kastel.vads.compiler.antlr.L2Parser;
import edu.kit.kastel.vads.compiler.parser.ast.FunctionTree;
import edu.kit.kastel.vads.compiler.parser.ast.ReturnTree;
import edu.kit.kastel.vads.compiler.parser.visitor.NoOpVisitor;
import edu.kit.kastel.vads.compiler.parser.visitor.Unit;

/// Checks that functions return.
/// Currently only works for straight-line code.
class ReturnAnalysis extends L2BaseListener {

    boolean returns = false;

    @Override
    public void enterControl(L2Parser.ControlContext ctx) {
        if (ctx.RETURN() != null)
            returns = true;
    }

    @Override
    public void exitProgram(L2Parser.ProgramContext ctx) {
        if (!returns)
            throw new SemanticException("program does not return");
    }
}
