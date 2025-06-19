package edu.kit.kastel.vads.compiler.myir.node;

import java.util.Collection;
import java.util.List;

public abstract class ConstantNode<T> implements PureExpressionNode {

    private final T value;

    public ConstantNode(T value) {
        this.value = value;
    }

    public T value() {
        return value;
    }

    @Override
    public Collection<PureExpressionNode> children() {
        return List.of();
    }
}
