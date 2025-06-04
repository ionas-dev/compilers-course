package edu.kit.kastel.vads.compiler.ir;

import edu.kit.kastel.vads.compiler.Span;
import edu.kit.kastel.vads.compiler.antlr.L2BaseVisitor;
import edu.kit.kastel.vads.compiler.antlr.L2Parser;
import edu.kit.kastel.vads.compiler.ir.node.Block;
import edu.kit.kastel.vads.compiler.ir.node.DivNode;
import edu.kit.kastel.vads.compiler.ir.node.ModNode;
import edu.kit.kastel.vads.compiler.ir.node.Node;
import edu.kit.kastel.vads.compiler.ir.optimize.Optimizer;
import edu.kit.kastel.vads.compiler.ir.util.DebugInfo;
import edu.kit.kastel.vads.compiler.ir.util.DebugInfoHelper;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.function.BinaryOperator;

import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.binaryOperator;
import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.identifier;
import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.parseInt;
import static edu.kit.kastel.vads.compiler.antlr.ParserRuleContextUtil.terminalNode;

/// SSA translation as described in
/// [`Simple and Efficient Construction of Static Single Assignment Form`](https://compilers.cs.uni-saarland.de/papers/bbhlmz13cc.pdf).
///
/// This implementation also tracks side effect edges that can be used to avoid reordering of operations that cannot be
/// reordered.
///
/// We recommend to read the paper to better understand the mechanics implemented here.
public class SsaTranslation {
    private final L2Parser.ProgramContext program;
    private final GraphConstructor constructor;

    public SsaTranslation(L2Parser.ProgramContext program, Optimizer optimizer) {
        this.program = program;
        this.constructor = new GraphConstructor(optimizer, "main");
    }

    public IrGraph translate() {
        var visitor = new SsaTranslationVisitor(this);
        this.program.accept(visitor);
        return this.constructor.graph();
    }

    private void writeVariable(String variable, Block block, Node value) {
        this.constructor.writeVariable(variable, block, value);
    }

    private Node readVariable(String variable, Block block) {
        return this.constructor.readVariable(variable, block);
    }

    private Block currentBlock() {
        return this.constructor.currentBlock();
    }

    private static class SsaTranslationVisitor extends L2BaseVisitor<Optional<Node>> {

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private static final Optional<Node> NOT_AN_EXPRESSION = Optional.empty();
        private final SsaTranslation data;
        private final Deque<DebugInfo> debugStack = new ArrayDeque<>();

        public SsaTranslationVisitor(SsaTranslation data) {
            this.data = data;
        }

        @Override
        public Optional<Node> visitAssignment(L2Parser.AssignmentContext ctx) {
            pushSpan(ctx);
            TerminalNode assignmentOperator = terminalNode(ctx.assignOperator()).orElseThrow();

            BinaryOperator<Node> desugar = switch (assignmentOperator.getSymbol().getType()) {
                case L2Parser.MINUS_ASSIGN -> data.constructor::newSub;
                case L2Parser.PLUS_ASSIGN -> data.constructor::newAdd;
                case L2Parser.TIMES_ASSIGN -> data.constructor::newMul;
                case L2Parser.DIV_ASSIGN -> (lhs, rhs) -> projResultDivMod(data, data.constructor.newDiv(lhs, rhs));
                case L2Parser.MOD_ASSIGN -> (lhs, rhs) -> projResultDivMod(data, data.constructor.newMod(lhs, rhs));
                case L2Parser.ASSIGN -> null;
                default ->
                        throw new IllegalArgumentException("not an assignment operator " + assignmentOperator.getText());
            };

            TerminalNode identifier = identifier(ctx.leftValue());
            Node rhs = ctx.expression().accept(this).orElseThrow();
            if (desugar != null) {
                rhs = desugar.apply(data.readVariable(identifier.getText(), data.currentBlock()), rhs);
            }
            data.writeVariable(identifier.getText(), data.currentBlock(), rhs);

            popSpan();
            return NOT_AN_EXPRESSION;
        }

        @Override
        public Optional<Node> visitUnaryExpression(L2Parser.UnaryExpressionContext ctx) {
            pushSpan(ctx);
            Node node = ctx.expression().accept(this).orElseThrow();
            Node res = data.constructor.newSub(data.constructor.newConstInt(0), node);
            popSpan();
            return Optional.of(res);
        }

        @Override
        public Optional<Node> visitBinaryExpression(L2Parser.BinaryExpressionContext ctx) {
            pushSpan(ctx);
            Node lhs = ctx.expression(0).accept(this).orElseThrow();
            Node rhs = ctx.expression(1).accept(this).orElseThrow();
            TerminalNode binaryOperator = binaryOperator(ctx);
            Node res = switch (binaryOperator.getSymbol().getType()) {
                case L2Parser.MINUS -> data.constructor.newSub(lhs, rhs);
                case L2Parser.PLUS -> data.constructor.newAdd(lhs, rhs);
                case L2Parser.TIMES -> data.constructor.newMul(lhs, rhs);
                case L2Parser.DIV -> projResultDivMod(data, data.constructor.newDiv(lhs, rhs));
                case L2Parser.MOD -> projResultDivMod(data, data.constructor.newMod(lhs, rhs));
                default ->
                        throw new IllegalArgumentException("not a binary expression operator " + binaryOperator.getText());
            };
            popSpan();
            return Optional.of(res);
        }

        @Override
        public Optional<Node> visitBlock(L2Parser.BlockContext ctx) {
            pushSpan(ctx);
            for (L2Parser.StatementContext statement : ctx.statement()) {
                statement.accept(this);
                // skip everything after a return in a block
                if (statement.control() != null && statement.control().return_() != null) {
                    break;
                }
            }
            popSpan();
            return NOT_AN_EXPRESSION;
        }

        @Override
        public Optional<Node> visitDeclaration(L2Parser.DeclarationContext ctx) {
            pushSpan(ctx);
            if (ctx.expression() != null) {
                Node rhs = ctx.expression().accept(this).orElseThrow();
                data.writeVariable(ctx.identifier().getText(), data.currentBlock(), rhs);
            }
            popSpan();
            return NOT_AN_EXPRESSION;
        }

        @Override
        public Optional<Node> visitProgram(L2Parser.ProgramContext ctx) {
            pushSpan(ctx);
            Node start = data.constructor.newStart();
            data.constructor.writeCurrentSideEffect(data.constructor.newSideEffectProj(start));
            ctx.block().accept(this);
            popSpan();
            return NOT_AN_EXPRESSION;
        }

        @Override
        public Optional<Node> visitIdentifier(L2Parser.IdentifierContext ctx) {
            pushSpan(ctx);
            Node value = data.readVariable(ctx.getText(), data.currentBlock());
            popSpan();
            return Optional.of(value);
        }

        @Override
        public Optional<Node> visitIntConstant(L2Parser.IntConstantContext ctx) {
            pushSpan(ctx);
            Node node = data.constructor.newConstInt((int) parseInt(ctx));
            popSpan();
            return Optional.of(node);
        }

        @Override
        public Optional<Node> visitLeftValue(L2Parser.LeftValueContext ctx) {
            return NOT_AN_EXPRESSION;
        }

        @Override
        public Optional<Node> visitReturn(L2Parser.ReturnContext ctx) {
            pushSpan(ctx);
            Node node = ctx.expression().accept(this).orElseThrow();
            Node ret = data.constructor.newReturn(node);
            data.constructor.graph().endBlock().addPredecessor(ret);
            popSpan();
            return NOT_AN_EXPRESSION;
        }
        @Override
        public Optional<Node> visitType(L2Parser.TypeContext ctx) {
            throw new UnsupportedOperationException();
        }

        private Node projResultDivMod(SsaTranslation data, Node divMod) {
            // make sure we actually have a div or a mod, as optimizations could
            // have changed it to something else already
            if (!(divMod instanceof DivNode || divMod instanceof ModNode)) {
                return divMod;
            }
            Node projSideEffect = data.constructor.newSideEffectProj(divMod);
            data.constructor.writeCurrentSideEffect(projSideEffect);
            return data.constructor.newResultProj(divMod);
        }

        private void pushSpan(ParserRuleContext ctx) {
            this.debugStack.push(DebugInfoHelper.getDebugInfo());
            DebugInfoHelper.setDebugInfo(new DebugInfo.SourceInfo(Span.fromStartAndEndToken(ctx.getStart(), ctx.getStop())));
        }

        private void popSpan() {
            DebugInfoHelper.setDebugInfo(this.debugStack.pop());
        }
    }


}
