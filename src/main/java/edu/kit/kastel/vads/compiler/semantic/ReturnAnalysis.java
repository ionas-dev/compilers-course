package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.antlr.L2BaseVisitor;
import edu.kit.kastel.vads.compiler.antlr.L2Parser;

public class ReturnAnalysis extends L2BaseVisitor<Boolean> {

    private int depth = 0;
    private boolean returned = false;

    @Override
    public Boolean visitProgram(L2Parser.ProgramContext ctx) {
        super.visitProgram(ctx);
        if (!returned) {
            throw new SemanticException("at least one control flow does not return");
        }
        return true;
    }

    @Override
    public Boolean visitFor(L2Parser.ForContext ctx) {
        return false;
    }

    @Override
    public Boolean visitWhile(L2Parser.WhileContext ctx) {
        return false;
    }

    @Override
    public Boolean visitAssignment(L2Parser.AssignmentContext ctx) {
        return false;
    }

    @Override
    public Boolean visitDeclaration(L2Parser.DeclarationContext ctx) {
        return super.visitDeclaration(ctx);
    }
    @Override
    public Boolean visitStatement(L2Parser.StatementContext ctx) {
        if (returned) {
            return true;
        }
        return super.visitStatement(ctx);
    }

    @Override
    public Boolean visitIf(L2Parser.IfContext ctx) {
        depth++;
        boolean ifReturns = ctx.statement().accept(this);
        boolean elseReturns = ctx.elseOptional().accept(this);
        depth--;
        if (depth == 0 && ifReturns && elseReturns) {
            returned = true;
        }
        return ifReturns && elseReturns;
    }

    @Override
    public Boolean visitElseOptional(L2Parser.ElseOptionalContext ctx) {
        if (ctx.statement() != null) {
            return ctx.statement().accept(this);
        }
        return false;
    }

    @Override
    public Boolean visitReturn(L2Parser.ReturnContext ctx) {
        if (depth == 0) {
            returned = true;
        }
        return true;
    }

    @Override
    protected Boolean defaultResult() {
        return false;
    }

    @Override
    protected Boolean aggregateResult(Boolean aggregate, Boolean nextResult) {
        return aggregate || nextResult;
    }
}
