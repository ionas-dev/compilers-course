package edu.kit.kastel.vads.compiler.myir.node.visitor;

import edu.kit.kastel.vads.compiler.myir.node.AssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.BinaryAssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.BooleanConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.CallAssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.IfNode;
import edu.kit.kastel.vads.compiler.myir.node.IntegerConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.JumpNode;
import edu.kit.kastel.vads.compiler.myir.node.LabelNode;
import edu.kit.kastel.vads.compiler.myir.node.Node;
import edu.kit.kastel.vads.compiler.myir.node.ProgramNode;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.ReturnNode;
import edu.kit.kastel.vads.compiler.myir.node.VariableNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.AddExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.BitwiseAndExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.BitwiseOrExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.BitwiseXorExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.DivisionExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.EqualExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.GreaterThanExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.LessThanExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.ModuloExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.MultiplyExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.NotEqualExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.ShiftLeftExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.ShiftRightExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.SubtractExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.block.BasicBlock;

import java.util.Collection;

public interface Visitor<T> {

    default T accumulateResults(Collection<T> results) {
        if (results.stream().findFirst().isEmpty()) {
            throw new RuntimeException("No result provided.");
        }
        return results.stream().findFirst().get();
    }

    default T visitNode(Node node) {
        return node.accept(this);
    }

    default T visitBasicBlock(BasicBlock node) {
        return node.accept(this);
    }

    default T visitAssignment(AssignmentNode node) {
        return node.accept(this);
    }

    default T visitBinaryAssignment(BinaryAssignmentNode node) {
        return node.accept(this);
    }

    default T visitBooleanConstant(BooleanConstantNode node) {
        return node.accept(this);
    }

    default T visitAddExpression(AddExpressionNode node) {
        return node.accept(this);
    }

    default T visitSubtractExpression(SubtractExpressionNode node) {
        return node.accept(this);
    }

    default T visitMultiplyExpression(MultiplyExpressionNode node) {
        return node.accept(this);
    }

    default T visitDivisionExpression(DivisionExpressionNode node) {
        return node.accept(this);
    }

    default T visitModuloExpression(ModuloExpressionNode node) {
        return node.accept(this);
    }

    default T visitBitwiseAndExpression(BitwiseAndExpressionNode node) {
        return node.accept(this);
    }

    default T visitBitwiseOrExpression(BitwiseOrExpressionNode node) {
        return node.accept(this);
    }

    default T visitBitwiseXorExpression(BitwiseXorExpressionNode node) {
        return node.accept(this);
    }

    default T visitEqualExpression(EqualExpressionNode node) {
        return node.accept(this);
    }

    default T visitNotEqualExpression(NotEqualExpressionNode node) {
        return node.accept(this);
    }

    default T visitGreaterThanExpression(GreaterThanExpressionNode node) {
        return node.accept(this);
    }

    default T visitLessThanExpression(LessThanExpressionNode node) {
        return node.accept(this);
    }

    default T visitShiftLeft(ShiftLeftExpressionNode node) {
        return node.accept(this);
    }

    default T visitShiftRight(ShiftRightExpressionNode node) {
        return node.accept(this);
    }

    default T visitCallAssignment(CallAssignmentNode node) {
        return node.accept(this);
    }

    default T visitIf(IfNode node) {
        return node.accept(this);
    }

    default T visitIntegerConstant(IntegerConstantNode node) {
        return node.accept(this);
    }

    default T visitJump(JumpNode node) {
        return node.accept(this);
    }

    default T visitLabel(LabelNode node) {
        return node.accept(this);
    }

    default T visitProgram(ProgramNode node) {
        return node.accept(this);
    }

    default T visitReturn(ReturnNode node) {
        return node.accept(this);
    }

    default T visitVariable(VariableNode node) {
        return node.accept(this);
    }



}
