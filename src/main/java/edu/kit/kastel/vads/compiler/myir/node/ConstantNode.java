package edu.kit.kastel.vads.compiler.myir.node;

import edu.kit.kastel.vads.compiler.myir.node.sealed.BooleanConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.IntegerConstantNode;

import java.util.List;

public abstract sealed class ConstantNode<T> implements PureExpressionNode permits BooleanConstantNode, IntegerConstantNode {

    private final T value;

    public ConstantNode(T value) {
        this.value = value;
    }

    public T value() {
        return value;
    }

    @Override
    public List<? extends Node> children() {
        return List.of();
    }
}
