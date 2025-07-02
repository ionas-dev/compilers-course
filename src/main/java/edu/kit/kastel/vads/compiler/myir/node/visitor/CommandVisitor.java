package edu.kit.kastel.vads.compiler.myir.node.visitor;

import edu.kit.kastel.vads.compiler.myir.node.Node;
import edu.kit.kastel.vads.compiler.myir.node.ProgramNode;
import edu.kit.kastel.vads.compiler.myir.node.block.BasicBlock;
import edu.kit.kastel.vads.compiler.myir.node.sealed.BooleanConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.IntegerConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.VariableNode;

public abstract class CommandVisitor<T> implements Visitor<T> {

    @Override
    public final T visitBasicBlock(BasicBlock node) {
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
    public T visitBooleanConstant(BooleanConstantNode node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }

    @Override
    public T visitVariable(VariableNode node) {
        throw new IllegalStateException(node.getClass().getSimpleName() + " is no command");
    }
}
