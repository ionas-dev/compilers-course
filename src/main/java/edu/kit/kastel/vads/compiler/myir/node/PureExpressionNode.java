package edu.kit.kastel.vads.compiler.myir.node;

public sealed interface PureExpressionNode extends PrimitiveNode permits ConstantNode, VariableNode, edu.kit.kastel.vads.compiler.myir.node.binop.AddExpressionNode, edu.kit.kastel.vads.compiler.myir.node.binop.BitwiseAndExpressionNode, edu.kit.kastel.vads.compiler.myir.node.binop.BitwiseOrExpressionNode, edu.kit.kastel.vads.compiler.myir.node.binop.BitwiseXorExpressionNode, edu.kit.kastel.vads.compiler.myir.node.binop.EqualExpressionNode, edu.kit.kastel.vads.compiler.myir.node.binop.GreaterThanExpressionNode, edu.kit.kastel.vads.compiler.myir.node.binop.LessThanExpressionNode, edu.kit.kastel.vads.compiler.myir.node.binop.MultiplyExpressionNode, edu.kit.kastel.vads.compiler.myir.node.binop.NotEqualExpressionNode, edu.kit.kastel.vads.compiler.myir.node.binop.ShiftLeftExpressionNode, edu.kit.kastel.vads.compiler.myir.node.binop.ShiftRightExpressionNode, edu.kit.kastel.vads.compiler.myir.node.binop.SubtractExpressionNode {

    default boolean sideEffect() {
        return false;
    }
}
