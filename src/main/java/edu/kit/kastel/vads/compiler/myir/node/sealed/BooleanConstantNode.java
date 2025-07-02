package edu.kit.kastel.vads.compiler.myir.node.sealed;

import edu.kit.kastel.vads.compiler.myir.node.ConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

public final class BooleanConstantNode extends ConstantNode<Boolean> {

    public BooleanConstantNode(boolean value) {
        super(value);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitBooleanConstant(this);
    }
}
