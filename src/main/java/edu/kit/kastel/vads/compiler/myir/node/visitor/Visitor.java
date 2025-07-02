package edu.kit.kastel.vads.compiler.myir.node.visitor;

import edu.kit.kastel.vads.compiler.myir.node.Node;
import edu.kit.kastel.vads.compiler.myir.node.ProgramNode;
import edu.kit.kastel.vads.compiler.myir.node.block.BasicBlock;
import edu.kit.kastel.vads.compiler.myir.node.sealed.AssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.BinaryAssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.BinaryExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.BooleanConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.CallAssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.IfNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.IntegerConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.JumpNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.LabelNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.ReturnNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.VariableNode;

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

    default T visitBinaryExpression(BinaryExpressionNode node) {
        return node.accept(this);
    }
}
