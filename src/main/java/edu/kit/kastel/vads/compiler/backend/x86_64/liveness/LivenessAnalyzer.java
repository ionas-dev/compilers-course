package edu.kit.kastel.vads.compiler.backend.x86_64.liveness;

import edu.kit.kastel.vads.compiler.backend.inssel.Instruction;
import edu.kit.kastel.vads.compiler.backend.liveness.ILivenessAnalyzer;
import edu.kit.kastel.vads.compiler.backend.regalloc.IRegister;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.BinaryOperationInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.MoveInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.MultiplyInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.ReturnInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.SignExtendInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.SignedDivisionInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.regalloc.Register;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class LivenessAnalyzer implements ILivenessAnalyzer {

    Map<Integer, Set<IRegister>> liveIn = new HashMap<>();

    @Override
    public Map<Integer, Set<IRegister>> computeLiveIn(List<Instruction> instructions) {
        ListIterator<Instruction> iterator = instructions.listIterator(instructions.size());
        int index = instructions.size() - 1;
        while (iterator.hasPrevious()) {
            Instruction instruction = iterator.previous();

            switch (instruction) {
                case MoveInstruction(IRegister source, IRegister target, _) -> {
                    kill(index, target);
                    gen(index, source);
                }
                case MoveInstruction(_, IRegister target, _) -> kill(index, target);
                case BinaryOperationInstruction binaryOperationInstruction -> {
                    // TODO: Fix type casting
                    gen(index, (IRegister) binaryOperationInstruction.target());
                    if (binaryOperationInstruction.source() instanceof IRegister) {
                        gen(index, (IRegister) binaryOperationInstruction.source());
                    }
                }
                case SignExtendInstruction _ -> gen(index, Register.ACCUMULATOR);
                case SignedDivisionInstruction(IRegister source, _) -> {
                    gen(index, Register.ACCUMULATOR);
                    gen(index, source);
                }
                case MultiplyInstruction(IRegister source, _) -> {
                    gen(index, Register.ACCUMULATOR);
                    gen(index, source);
                }
                case SignedDivisionInstruction _, MultiplyInstruction _, ReturnInstruction _ -> gen(index, Register.ACCUMULATOR);
                default -> {}
            }

            liveIn.computeIfAbsent(index - 1, _ -> new HashSet<>()).addAll(liveIn.get(index));
            index--;
        }

        return liveIn;
    }

    private void gen(int index, IRegister instructionTarget) {
        liveIn.computeIfAbsent(index, _ -> new HashSet<>()).add(instructionTarget);
    }

    private void kill(int index, IRegister instructionTarget) {
        liveIn.computeIfAbsent(index, _ -> new HashSet<>()).remove(instructionTarget);
    }
}
