package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.antlr.L2BaseVisitor;
import edu.kit.kastel.vads.compiler.antlr.L2Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.assignmentOperator;
import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.identifier;

/// Checks that variables are
/// - declared before assignment
/// - not declared twice
/// - not initialized twice
/// - assigned before referenced
/// TODO: Visit only elaborated syntax to reduce visitors
class VariableStatusAnalysis extends L2BaseVisitor<Void> {

    // maps the depth of control blocks to a mapping of identifier to status
    private final Map<Integer, Map<String, VariableStatus>> status = new HashMap<>();
    private int depth = 0;

    public VariableStatusAnalysis() {
        status.put(depth, new HashMap<>());
    }

    @Override
    public Void visitAssignment(L2Parser.AssignmentContext ctx) {
        ctx.expression().accept(this);
        TerminalNode identifier = identifier(ctx.leftValue());
        VariableStatus status = getStatus(depth, identifier.getText());
        TerminalNode assignmentOperator = assignmentOperator(ctx);

        if (assignmentOperator.getSymbol().getType() == L2Parser.ASSIGN) {
            checkDeclared(identifier, status);
        } else {
            checkInitialized(identifier, status);
        }

        if (status != VariableStatus.INITIALIZED) {
            updateStatus(depth, identifier, VariableStatus.INITIALIZED);
        }
        return null;
    }

    @Override
    public Void visitDeclaration(L2Parser.DeclarationContext ctx) {
        TerminalNode identifier = ctx.identifier().IDENT();
        checkUndeclared(identifier, getStatus(depth, identifier.getText()));
        if (ctx.expression() != null) {
            updateStatus(depth, identifier, VariableStatus.INITIALIZED);
            return ctx.expression().accept(this);
        }
        updateStatus(depth, identifier, VariableStatus.DECLARED);
        return null;
    }

    @Override
    public Void visitIdentifier(L2Parser.IdentifierContext ctx) {
        TerminalNode identifier = ctx.IDENT();
        VariableStatus status = getStatus(depth, identifier.getText());
        checkInitialized(identifier, status);
        return super.visitIdentifier(ctx);
    }

    @Override
    public Void visitIf(L2Parser.IfContext ctx) {
        return visitLoop(ctx);
    }

    @Override
    public Void visitWhile(L2Parser.WhileContext ctx) {
        return visitLoop(ctx);
    }

    @Override
    public Void visitFor(L2Parser.ForContext ctx) {
        L2Parser.SimpleContext simpleContext = ctx.simpleOptional(1).simple();
        if (simpleContext != null && simpleContext.declaration() != null) {
            throw new SemanticException("for step must not be a declaration");
        }
        return visitLoop(ctx);
    }

    private Void visitLoop(ParserRuleContext ctx) {
        depth++;
        status.put(depth, new HashMap<>(status.get(depth - 1)));
        super.visitChildren(ctx);
        depth--;
        return null;
    }

    private static void checkDeclared(TerminalNode identifier, @Nullable VariableStatus status) {
        if (status == null) {
            throw new SemanticException("Variable " + identifier + " must be declared before assignment");
        }
    }

    private static void checkInitialized(TerminalNode identifier, @Nullable VariableStatus status) {
        if (status == null || status == VariableStatus.DECLARED) {
            throw new SemanticException("Variable " + identifier + " must be initialized before use");
        }
    }

    private static void checkUndeclared(TerminalNode identifier, @Nullable VariableStatus status) {
        if (status != null) {
            throw new SemanticException("Variable " + identifier + " is already declared");
        }
    }

    private void updateStatus(int depth, TerminalNode identifier, VariableStatus status) {
        this.status.get(depth).merge(identifier.getText(), status, (existing, replacement) -> {
            if (existing.ordinal() >= replacement.ordinal()) {
                throw new SemanticException("variable is already " + existing + ". Cannot be " + replacement + " here.");
            }
            return replacement;
        });
    }

    @Nullable
    private VariableStatus getStatus(int depth, String identifier) {
        return status.get(depth).get(identifier);
    }

    enum VariableStatus {
        DECLARED,
        INITIALIZED;

        @Override
        public String toString() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
