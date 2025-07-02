package edu.kit.kastel.vads.compiler.myir.node;

import edu.kit.kastel.vads.compiler.myir.node.sealed.AssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.BinaryAssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.CallAssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.IfNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.JumpNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.LabelNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.ReturnNode;

public sealed interface CommandNode extends PrimitiveNode permits AssignmentNode, BinaryAssignmentNode, CallAssignmentNode, IfNode, JumpNode, LabelNode, ReturnNode {

    @Override
    default boolean sideEffect() {
        return true;
    }
}
