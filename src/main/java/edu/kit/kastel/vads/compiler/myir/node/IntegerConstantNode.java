package edu.kit.kastel.vads.compiler.myir.node;

import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

public final class IntegerConstantNode extends ConstantNode<Long> {

    public IntegerConstantNode(long value) {
        super(value);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitIntegerConstant(this);
    }
}
