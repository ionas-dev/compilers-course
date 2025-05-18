package edu.kit.kastel.vads.compiler.backend.x86_64.regalloc;

import edu.kit.kastel.vads.compiler.backend.common.operand.Operand;
import edu.kit.kastel.vads.compiler.backend.x86_64.operand.ImmediateOperand;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.BinaryOperationInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.CallInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.Comment;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.EmptyStatement;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.GlobalDirective;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.Label;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.MoveInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.SignedMultiplyInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.ReturnInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.SignExtendInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.SignedDivisionInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.operand.Register;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.SyscallInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.TextDirective;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.X86Statement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class LivenessAnalyzer {

    Map<Integer, Set<Operand>> liveIn = new HashMap<>();

    public Map<Integer, Set<Operand>> computeLiveIn(List<X86Statement> instructions) {
        ListIterator<X86Statement> iterator = instructions.listIterator(instructions.size());
        int line = instructions.size() - 1;
        while (iterator.hasPrevious()) {
            X86Statement instruction = iterator.previous();

            switch (instruction) {
                case MoveInstruction moveInstruction -> {
                    kill(line, moveInstruction.target());
                    gen(line, moveInstruction.source());
                }
                case BinaryOperationInstruction binaryOperationInstruction -> {
                    gen(line, binaryOperationInstruction.target());
                    gen(line, binaryOperationInstruction.source());
                }
                case SignExtendInstruction _ -> gen(line, Register.ACCUMULATOR);
                case SignedDivisionInstruction signedDivisionInstruction -> {
                    gen(line, Register.ACCUMULATOR);
                    gen(line, signedDivisionInstruction.source());
                }
                case SignedMultiplyInstruction signedMultiplyInstruction -> {
                    gen(line, Register.ACCUMULATOR);
                    gen(line, signedMultiplyInstruction.source());
                }
                case ReturnInstruction _ -> gen(line, Register.ACCUMULATOR);
                case CallInstruction _, Comment _, EmptyStatement _, GlobalDirective _, Label _, SyscallInstruction _,
                     TextDirective _ -> {
                }
            }

            liveIn.computeIfAbsent(line - 1, _ -> new HashSet<>()).addAll(liveIn.get(line));
            line--;
        }

        return liveIn;
    }

    private void gen(int index, Operand instructionTarget) {
        if (!(instructionTarget instanceof ImmediateOperand)) {
            liveIn.computeIfAbsent(index, _ -> new HashSet<>()).add(instructionTarget);
        }
    }

    private void kill(int index, Operand instructionTarget) {
        liveIn.computeIfAbsent(index, _ -> new HashSet<>()).remove(instructionTarget);
    }
}
