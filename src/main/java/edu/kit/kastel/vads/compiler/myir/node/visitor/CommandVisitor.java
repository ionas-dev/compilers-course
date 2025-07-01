package edu.kit.kastel.vads.compiler.myir.node.visitor;

import edu.kit.kastel.vads.compiler.myir.node.BooleanConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.IntegerConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.Node;
import edu.kit.kastel.vads.compiler.myir.node.ProgramNode;
import edu.kit.kastel.vads.compiler.myir.node.VariableNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.AddExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.BitwiseAndExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.BitwiseOrExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.BitwiseXorExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.EqualExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.GreaterThanExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.LessThanExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.MultiplyExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.NotEqualExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.ShiftLeftExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.ShiftRightExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.SubtractExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.block.BasicBlock;

public abstract class CommandVisitor<T> implements Visitor<T> {

    @Override
    public final T visitBasicBlock(BasicBlock node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }

    @Override
    public T visitBitwiseAndExpression(BitwiseAndExpressionNode node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }

    @Override
    public T visitBitwiseOrExpression(BitwiseOrExpressionNode node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }

    @Override
    public T visitNotEqualExpression(NotEqualExpressionNode node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }

    @Override
    public T visitEqualExpression(EqualExpressionNode node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }

    @Override
    public T visitLessThanExpression(LessThanExpressionNode node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }

    @Override
    public T visitShiftLeft(ShiftLeftExpressionNode node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }

    @Override
    public T visitShiftRight(ShiftRightExpressionNode node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }

    @Override
    public T visitSubtractExpression(SubtractExpressionNode node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }

    @Override
    public T visitAddExpression(AddExpressionNode node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }

    @Override
    public T visitIntegerConstant(IntegerConstantNode node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }

    @Override
    public T visitProgram(ProgramNode node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }

    @Override
    public T visitNode(Node node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }

    @Override
    public T visitMultiplyExpression(MultiplyExpressionNode node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }

    @Override
    public T visitGreaterThanExpression(GreaterThanExpressionNode node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }

    @Override
    public T visitBitwiseXorExpression(BitwiseXorExpressionNode node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }

    @Override
    public T visitBooleanConstant(BooleanConstantNode node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }

    @Override
    public T visitVariable(VariableNode node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }
}
