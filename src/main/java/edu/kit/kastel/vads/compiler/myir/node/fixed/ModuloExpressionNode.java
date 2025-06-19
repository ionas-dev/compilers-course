package edu.kit.kastel.vads.compiler.myir.node.fixed;

import edu.kit.kastel.vads.compiler.myir.node.BinaryExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.Command;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

public final class ModuloExpressionNode extends BinaryExpressionNode implements Command<PureExpressionNode> {

    public ModuloExpressionNode(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }
}
