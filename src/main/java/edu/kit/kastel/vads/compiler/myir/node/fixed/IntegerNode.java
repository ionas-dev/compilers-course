package edu.kit.kastel.vads.compiler.myir.node.fixed;

import edu.kit.kastel.vads.compiler.myir.node.ConstantNode;

public final class IntegerNode extends ConstantNode<Integer> {

    public IntegerNode(int value) {
        super(value);
    }
}
