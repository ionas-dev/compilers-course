package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.antlr.L2BaseListener;
import edu.kit.kastel.vads.compiler.antlr.L2BaseVisitor;
import edu.kit.kastel.vads.compiler.antlr.L2Parser;
import edu.kit.kastel.vads.compiler.lexer.Operator;
import edu.kit.kastel.vads.compiler.parser.ast.AssignmentTree;
import edu.kit.kastel.vads.compiler.parser.ast.DeclarationTree;
import edu.kit.kastel.vads.compiler.parser.ast.IdentExpressionTree;
import edu.kit.kastel.vads.compiler.parser.ast.LValueIdentTree;
import edu.kit.kastel.vads.compiler.parser.ast.NameTree;
import edu.kit.kastel.vads.compiler.parser.visitor.NoOpVisitor;
import edu.kit.kastel.vads.compiler.parser.visitor.Unit;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.identifier;

/// Checks that variables are
/// - declared before assignment
/// - not declared twice
/// - not initialized twice
/// - assigned before referenced
class VariableStatusAnalysis extends L2BaseVisitor<Void> {

    private final Map<String, VariableStatus> data = new HashMap<>();

    @Override
    public Void visitAssignment(L2Parser.AssignmentContext ctx) {
        TerminalNode identifier = identifier(ctx.leftValue());
        VariableStatus status = data.get(identifier.getText());
        if (ctx.assignOperator().ASSIGN() != null) {
            checkDeclared(identifier, status);
        } else {
            checkInitialized(identifier, status);
        }
        if (status != VariableStatus.INITIALIZED) {
            updateStatus(identifier, VariableStatus.INITIALIZED);
        }
        return ctx.expression().accept(this);
    }

    @Override
    public Void visitDeclaration(L2Parser.DeclarationContext ctx) {
        TerminalNode identifier = ctx.identifier().IDENT();
        checkUndeclared(identifier, data.get(identifier.getText()));
        if (ctx.expression() != null) {
            updateStatus(identifier, VariableStatus.INITIALIZED);
            ctx.expression().accept(this);
        }
        updateStatus(identifier, VariableStatus.DECLARED);
        return null;
    }

    @Override
    public Void visitIdentifier(L2Parser.IdentifierContext ctx) {
        TerminalNode identifier = ctx.IDENT();
        VariableStatus status = data.get(identifier.getText());
        checkInitialized(identifier, status);
        return super.visitIdentifier(ctx);
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

    private void updateStatus(TerminalNode identifier, VariableStatus status) {
        this.data.merge(identifier.getText(), status, (existing, replacement) -> {
            if (existing.ordinal() >= replacement.ordinal()) {
                throw new SemanticException("variable is already " + existing + ". Cannot be " + replacement + " here.");
            }
            return replacement;
        });
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
