package edu.kit.kastel.vads.compiler.backend.x86_64.liveness;

import edu.kit.kastel.vads.compiler.backend.inssel.BitSize;
import edu.kit.kastel.vads.compiler.backend.inssel.Instruction;
import edu.kit.kastel.vads.compiler.backend.inssel.InstructionTarget;
import edu.kit.kastel.vads.compiler.backend.liveness.ILivenessAnalyzer;
import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.BinaryOpInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.MoveInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.ReturnInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.SignExtendInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.SignedDivInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.regalloc.ReturnRegister;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class LivenessAnalyzer implements ILivenessAnalyzer {

    Map<Integer, Set<InstructionTarget>> liveIn = new HashMap<>();

    @Override
    public Map<Integer, Set<InstructionTarget>> execute(List<Instruction> instructions) {
        ListIterator<Instruction> iterator = instructions.listIterator(instructions.size());
        int index = instructions.size() - 1;
        while (iterator.hasPrevious()) {
            Instruction instruction = iterator.previous();

            switch (instruction) {
                case MoveInstruction moveInstruction -> {
                    kill(index, moveInstruction.target());
                    if (moveInstruction.source() instanceof Register) {
                        gen(index, moveInstruction.source());
                    }
                }
                case BinaryOpInstruction binaryOpInstruction -> {
                    gen(index, binaryOpInstruction.target());
                    if (binaryOpInstruction.source() instanceof Register) {
                        gen(index, binaryOpInstruction.source());
                    }
                }
                case SignExtendInstruction _ -> gen(index, new ReturnRegister(BitSize.BIT32));
                case SignedDivInstruction signedDivInstruction -> {
                    gen(index, new ReturnRegister(BitSize.BIT32));
                    if (signedDivInstruction.source() instanceof Register) {
                        gen(index, signedDivInstruction.source());
                    }
                }
                case ReturnInstruction _ -> gen(index, new ReturnRegister(BitSize.BIT32));
                default -> {}
            }

            liveIn.computeIfAbsent(index - 1, _ -> new HashSet<>()).addAll(liveIn.get(index));
            index--;
        }

        return liveIn;
    }

    private void gen(int index, InstructionTarget instructionTarget) {
        liveIn.computeIfAbsent(index, _ -> new HashSet<>()).add(instructionTarget);
    }

    private void kill(int index, InstructionTarget instructionTarget) {
        liveIn.computeIfAbsent(index, _ -> new HashSet<>()).remove(instructionTarget);
    }
}
