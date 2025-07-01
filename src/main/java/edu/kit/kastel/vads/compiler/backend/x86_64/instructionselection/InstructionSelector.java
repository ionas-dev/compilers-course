package edu.kit.kastel.vads.compiler.backend.x86_64.instructionselection;

import edu.kit.kastel.vads.compiler.backend.common.operand.Operand;
import edu.kit.kastel.vads.compiler.backend.common.util.BitSize;
import edu.kit.kastel.vads.compiler.backend.x86_64.operand.ImmediateOperand;
import edu.kit.kastel.vads.compiler.backend.x86_64.operand.Register;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.BinaryOperationInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.MoveInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.ReturnInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.SignExtendInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.SignedDivisionInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.SignedMultiplyInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.X86Statement;
import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.AddNode;
import edu.kit.kastel.vads.compiler.ir.node.BinaryOperationNode;
import edu.kit.kastel.vads.compiler.ir.node.Block;
import edu.kit.kastel.vads.compiler.ir.node.ConstIntNode;
import edu.kit.kastel.vads.compiler.ir.node.DivNode;
import edu.kit.kastel.vads.compiler.ir.node.ModNode;
import edu.kit.kastel.vads.compiler.ir.node.MulNode;
import edu.kit.kastel.vads.compiler.ir.node.Node;
import edu.kit.kastel.vads.compiler.ir.node.Phi;
import edu.kit.kastel.vads.compiler.ir.node.ProjNode;
import edu.kit.kastel.vads.compiler.ir.node.ReturnNode;
import edu.kit.kastel.vads.compiler.ir.node.StartNode;
import edu.kit.kastel.vads.compiler.ir.node.SubNode;
import edu.kit.kastel.vads.compiler.semantic.SemanticException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static edu.kit.kastel.vads.compiler.ir.util.NodeSupport.predecessorSkipProj;

public class InstructionSelector {

    public static List<X86Statement> selectInstructions(IrGraph graph, Map<Node, Operand> operands) {
        List<X86Statement> instructions = new ArrayList<>();
        Set<Node> visited = new HashSet<>();
        scan(graph.endBlock(), visited, instructions, operands);
        return instructions;
    }

    private static void scan(Node node, Set<Node> visited, List<X86Statement> instructions, Map<Node, Operand> operands) throws SemanticException {
        for (Node predecessor : node.predecessors()) {
            if (visited.add(predecessor)) {
                scan(predecessor, visited, instructions, operands);
            }
        }

        instructions.addAll(selectInstructions(node, operands));
    }

    // TODO: Exchange hard-coded size
    private static List<X86Statement> selectInstructions(Node node, Map<Node, Operand> operands) {
        return switch (node) {
            case AddNode _ -> toBinaryInstructions(operands, node, BinaryOperationInstruction.Operation.ADD);
            case SubNode _ -> toBinaryInstructions(operands, node, BinaryOperationInstruction.Operation.SUB);
            case MulNode _ -> toMultiplyInstructions(operands, node);
            case DivNode _ -> toDivInstructions(operands, node);
            case ModNode _ -> toModInstructions(operands, node);
            case ReturnNode _ -> toReturnInstructions(operands, node);
            case ConstIntNode constNode -> toConstInstructions(operands, constNode);
            case Phi _ -> throw new SemanticException(node.toString());
            case StartNode _, Block _, ProjNode _ -> List.of();
        };
    }

    private static List<X86Statement> toConstInstructions(Map<Node, Operand> registers, ConstIntNode node) {
        return List.of(new MoveInstruction(new ImmediateOperand(node.value()), registers.get(node), BitSize.BIT32));
    }

    private static List<X86Statement> toReturnInstructions(Map<Node, Operand> registers, Node node) {
        return List.of(
                new MoveInstruction(registers.get(predecessorSkipProj(node, ReturnNode.RESULT)), Register.ACCUMULATOR, BitSize.BIT32),
                new ReturnInstruction()
        );
    }

    private static List<X86Statement> toModInstructions(Map<Node, Operand> registers, Node node) {
        return List.of(
                new MoveInstruction(Register.DATA, registers.get(node), BitSize.BIT32),
                new MoveInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)), Register.ACCUMULATOR, BitSize.BIT32),
                new SignExtendInstruction(),
                new SignedDivisionInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)), BitSize.BIT32),
                new MoveInstruction(Register.DATA, Register.ACCUMULATOR, BitSize.BIT32),
                new MoveInstruction(registers.get(node), Register.DATA, BitSize.BIT32),
                new MoveInstruction(Register.ACCUMULATOR, registers.get(node), BitSize.BIT32)
        );
    }

    private static List<X86Statement> toDivInstructions(Map<Node, Operand> registers, Node node) {
        return List.of(
                new MoveInstruction(Register.DATA, registers.get(node), BitSize.BIT32),
                new MoveInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)), Register.ACCUMULATOR, BitSize.BIT32),
                new SignExtendInstruction(),
                new SignedDivisionInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)), BitSize.BIT32),
                new MoveInstruction(registers.get(node), Register.DATA, BitSize.BIT32),
                new MoveInstruction(Register.ACCUMULATOR, registers.get(node), BitSize.BIT32)
        );
    }

    private static List<X86Statement> toMultiplyInstructions(Map<Node, Operand> registers, Node node) {
        return List.of(
                new MoveInstruction(Register.DATA, registers.get(node), BitSize.BIT32),
                new MoveInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)), Register.ACCUMULATOR, BitSize.BIT32),
                new SignedMultiplyInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)), BitSize.BIT32),
                new MoveInstruction(registers.get(node), Register.DATA, BitSize.BIT32),
                new MoveInstruction(Register.ACCUMULATOR, registers.get(node), BitSize.BIT32)
        );
    }

    private static List<X86Statement> toBinaryInstructions(Map<Node, Operand> registers, Node node, BinaryOperationInstruction.Operation operation) {
        return List.of(
                new MoveInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)), registers.get(node), BitSize.BIT32),
                new BinaryOperationInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)), registers.get(node), operation, BitSize.BIT32)
        );
    }
}
