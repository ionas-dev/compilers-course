package edu.kit.kastel.vads.compiler.myir.node.fixed;

import edu.kit.kastel.vads.compiler.myir.node.ConstantNode;

public final class BooleanNode extends ConstantNode<Boolean> {

    public BooleanNode(boolean value) {
        super(value);
    }
}
