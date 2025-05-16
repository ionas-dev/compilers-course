package edu.kit.kastel.vads.compiler.backend.x86_64.inssel;

import edu.kit.kastel.vads.compiler.backend.inssel.BitSize;
import edu.kit.kastel.vads.compiler.backend.inssel.ConstInstructionTarget;
import edu.kit.kastel.vads.compiler.backend.inssel.IInstructionSelector;
import edu.kit.kastel.vads.compiler.backend.inssel.Instruction;
import edu.kit.kastel.vads.compiler.backend.regalloc.IRegister;
import edu.kit.kastel.vads.compiler.backend.regalloc.VirtualRegisterAllocator;
import edu.kit.kastel.vads.compiler.backend.x86_64.regalloc.Register;
import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.AddNode;
import edu.kit.kastel.vads.compiler.ir.node.BinaryOperationNode;
import edu.kit.kastel.vads.compiler.ir.node.ConstIntNode;
import edu.kit.kastel.vads.compiler.ir.node.DivNode;
import edu.kit.kastel.vads.compiler.ir.node.ModNode;
import edu.kit.kastel.vads.compiler.ir.node.MulNode;
import edu.kit.kastel.vads.compiler.ir.node.Node;
import edu.kit.kastel.vads.compiler.ir.node.Phi;
import edu.kit.kastel.vads.compiler.ir.node.ReturnNode;
import edu.kit.kastel.vads.compiler.ir.node.SubNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static edu.kit.kastel.vads.compiler.ir.util.NodeSupport.predecessorSkipProj;

public class InstructionSelector implements IInstructionSelector {

    @Override
    public List<Instruction> transform(IrGraph graph) throws UnsupportedOperationException {
        List<Instruction> instructions = new ArrayList<>();
        instructions.add(() -> "_" + graph.name() + ":");

        VirtualRegisterAllocator allocator = new VirtualRegisterAllocator();
        Map<Node, IRegister> registers = allocator.allocateRegisters(graph);
        Set<Node> visited = new HashSet<>();
        scan(graph.endBlock(), visited, instructions, registers);
        return instructions;
    }

    private void scan(Node node, Set<Node> visited, List<Instruction> instructions, Map<Node, IRegister> registers)
            throws UnsupportedOperationException
    {
        for (Node predecessor : node.predecessors()) {
            if (visited.add(predecessor)) {
                scan(predecessor, visited, instructions, registers);
            }
        }

        // TODO: Exchange hard-coded size
        switch (node) {
            case AddNode addNode -> binary(instructions, registers, addNode, BinaryOperationInstruction.Operation.ADD);
            case SubNode subNode -> binary(instructions, registers, subNode, BinaryOperationInstruction.Operation.SUB);
            case MulNode _ -> {
                instructions.add(new MoveInstruction(Register.DATA, registers.get(node), BitSize.BIT32));
                instructions.add(new MoveInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)), Register.ACCUMULATOR, BitSize.BIT32));
                instructions.add(new MultiplyInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)), BitSize.BIT32));
                instructions.add(new MoveInstruction(registers.get(node), Register.DATA, BitSize.BIT32));
                instructions.add(new MoveInstruction(Register.ACCUMULATOR, registers.get(node), BitSize.BIT32));
            }
            case DivNode _ -> {
                instructions.add(new MoveInstruction(Register.DATA, registers.get(node), BitSize.BIT32));
                instructions.add(new MoveInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)), Register.ACCUMULATOR, BitSize.BIT32));
                instructions.add(new SignExtendInstruction());
                instructions.add(new SignedDivisionInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)), BitSize.BIT32));
                instructions.add(new MoveInstruction(registers.get(node), Register.DATA, BitSize.BIT32));
                instructions.add(new MoveInstruction(Register.ACCUMULATOR, registers.get(node), BitSize.BIT32));
            }
            case ModNode _ -> {
                // TODO: Extract this and DivNode case
                // TODO: Reduce unnecessary instructions
                instructions.add(new MoveInstruction(Register.DATA, registers.get(node), BitSize.BIT32));
                instructions.add(new MoveInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)), Register.ACCUMULATOR, BitSize.BIT32));
                instructions.add(new SignExtendInstruction());
                instructions.add(new SignedDivisionInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)), BitSize.BIT32));
                instructions.add(new MoveInstruction(Register.DATA, Register.ACCUMULATOR, BitSize.BIT32));
                instructions.add(new MoveInstruction(registers.get(node), Register.DATA, BitSize.BIT32));
                instructions.add(new MoveInstruction(Register.ACCUMULATOR, registers.get(node), BitSize.BIT32));
            }
            case ReturnNode _ -> {
                instructions.add(new MoveInstruction(registers.get(predecessorSkipProj(node, ReturnNode.RESULT)), Register.ACCUMULATOR, BitSize.BIT32));
                instructions.add(new ReturnInstruction());
            }
            case ConstIntNode constNode -> instructions.add(new MoveInstruction(new ConstInstructionTarget(constNode.value()), registers.get(constNode), BitSize.BIT32));
            case Phi _ -> throw new UnsupportedOperationException(node.toString());
            default -> {}
        }
    }

    private static void binary(
            List<Instruction> instructions,
            Map<Node, IRegister> registers,
            BinaryOperationNode node,
            BinaryOperationInstruction.Operation operation
    ) {
        instructions.add(new MoveInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)), registers.get(node), BitSize.BIT32));
        instructions.add(new BinaryOperationInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)), registers.get(node), operation, BitSize.BIT32));
    }
}
