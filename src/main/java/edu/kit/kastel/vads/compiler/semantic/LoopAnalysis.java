package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.antlr.L2BaseVisitor;
import edu.kit.kastel.vads.compiler.antlr.L2Parser;

/// Checks that functions return.
/// Currently only works for straight-line code.
class LoopAnalysis extends L2BaseVisitor<Void> {

    private int loopDepth = 0;

    @Override
    public Void visitFor(L2Parser.ForContext ctx) {
        loopDepth++;
        super.visitFor(ctx);
        loopDepth--;
        return null;
    }

    @Override
    public Void visitWhile(L2Parser.WhileContext ctx) {
        loopDepth++;
        super.visitWhile(ctx);
        loopDepth--;
        return null;
    }

    @Override
    public Void visitBreak(L2Parser.BreakContext ctx) {
        if (loopDepth == 0) {
            throw new SemanticException("break can only be called within loops");
        }
        return super.visitBreak(ctx);
    }

    @Override
    public Void visitContinue(L2Parser.ContinueContext ctx) {
        if (loopDepth == 0) {
            throw new SemanticException("break can only be called within loops");
        }
        return super.visitContinue(ctx);
    }
}
