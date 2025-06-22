package edu.kit.kastel.vads.compiler.myir.node.binop;

import edu.kit.kastel.vads.compiler.myir.node.Command;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;

public final class DivisionExpression extends BinaryExpressionNode implements Command {

    public DivisionExpression(PureExpressionNode left, PureExpressionNode right) {
        super(left, right);
    }
}
