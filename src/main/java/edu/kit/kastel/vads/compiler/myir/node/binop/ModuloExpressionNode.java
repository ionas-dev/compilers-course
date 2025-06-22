package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.Command;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

public final class ModuloExpressionNode extends BinaryExpressionNode implements Command {

    public ModuloExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }
}
