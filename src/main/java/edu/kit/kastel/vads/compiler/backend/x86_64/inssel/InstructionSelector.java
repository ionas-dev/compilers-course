package edu.kit.kastel.vads.compiler.backend.x86_64.inssel;

import edu.kit.kastel.vads.compiler.backend.inssel.BitSize;
import edu.kit.kastel.vads.compiler.backend.inssel.ConstInstructionTarget;
import edu.kit.kastel.vads.compiler.backend.inssel.IInstructionSelector;
import edu.kit.kastel.vads.compiler.backend.inssel.Instruction;
import edu.kit.kastel.vads.compiler.backend.inssel.InstructionTarget;
import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.regalloc.VirtualRegisterAllocator;
import edu.kit.kastel.vads.compiler.backend.x86_64.regalloc.BaseAddressRegister;
import edu.kit.kastel.vads.compiler.backend.x86_64.regalloc.ReturnRegister;
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
    public List<Instruction> transform(IrGraph graph) {
        List<Instruction> instructions = new ArrayList<>();
        instructions.add(() -> "_" + graph.name() + ":");

        VirtualRegisterAllocator allocator = new VirtualRegisterAllocator();
        Map<Node, Register> registers = allocator.allocateRegisters(graph);
        Set<Node> visited = new HashSet<>();
        scan(graph.endBlock(), visited, instructions, registers);
        return instructions;
    }

    private void scan(Node node, Set<Node> visited, List<Instruction> instructions, Map<Node, Register> registers) {
        for (Node predecessor : node.predecessors()) {
            if (visited.add(predecessor)) {
                scan(predecessor, visited, instructions, registers);
            }
        }

        switch (node) {
            case AddNode addNode -> binary(instructions, registers, addNode, AddInstruction.class);
            case SubNode subNode -> binary(instructions, registers, subNode, SubInstruction.class);
            case MulNode mulNode -> binary(instructions, registers, mulNode, MulInstruction.class);
            case DivNode _ -> {
                // TODO: Exchange hard-coded size
                instructions.add(new MoveInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)), new ReturnRegister(BitSize.BIT32)));
                instructions.add(new SignExtendInstruction());
                instructions.add(new SignedDivInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT))));
                instructions.add(new MoveInstruction(new ReturnRegister(BitSize.BIT32), registers.get(node)));
            }
            case ModNode _ -> {
                // TODO: Exchange hard-coded size
                // TODO: Extract this and DivNode case
                instructions.add(new MoveInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)), new ReturnRegister(BitSize.BIT32)));
                instructions.add(new SignExtendInstruction());
                instructions.add(new SignedDivInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT))));
                instructions.add(new MoveInstruction(new BaseAddressRegister(BitSize.BIT32), registers.get(node)));
            }
            case ReturnNode _ -> {
                // TODO: Exchange hard-coded size
                instructions.add(new MoveInstruction(registers.get(predecessorSkipProj(node, ReturnNode.RESULT)), new ReturnRegister(BitSize.BIT32)));
                instructions.add(new ReturnInstruction());
            }
            case ConstIntNode constNode -> instructions.add(new MoveInstruction(new ConstInstructionTarget(constNode.value()), registers.get(constNode)));
            case Phi _ -> throw new UnsupportedOperationException("phi");
            default -> {}
        }
    }

    private static void binary(List<Instruction> instructions, Map<Node, Register> registers, BinaryOperationNode node, Class<? extends BinaryOpInstruction> instructionClass) {
        instructions.add(new MoveInstruction(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)), registers.get(node)));
        try {
            instructions.add(
                    instructionClass.getDeclaredConstructor(InstructionTarget.class, InstructionTarget.class)
                            .newInstance(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)), registers.get(node)));
        } catch (Exception e) {
            assert false : "Class " + instructionClass.getSimpleName() + " has no default constructor.";
        }
    }
}
