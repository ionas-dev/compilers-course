package edu.kit.kastel.vads.compiler.backend.x86_64.codegen;

import edu.kit.kastel.vads.compiler.backend.codegen.ICodeGenerator;
import edu.kit.kastel.vads.compiler.backend.inssel.Instruction;
import edu.kit.kastel.vads.compiler.backend.inssel.IInstructionSelector;
import edu.kit.kastel.vads.compiler.backend.inssel.InstructionTarget;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.InstructionSelector;
import edu.kit.kastel.vads.compiler.backend.x86_64.liveness.LivenessAnalyzer;
import edu.kit.kastel.vads.compiler.ir.IrGraph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CodeGenerator implements ICodeGenerator {

    @Override
    public String generateCode(List<IrGraph> program) {
        IInstructionSelector instructionSelector = new InstructionSelector();

        List<Instruction> instructions = new ArrayList<>(List.of(
                () -> ".global main",
                () -> ".global _main",
                () -> ".text",
                () -> "",
                () -> "main:",
                () -> "call _main",
                () -> "",
                () -> "movq %rax, %rdi",
                () -> "movq $0x3C, %rax",
                () -> "syscall"
                ));

        for (IrGraph graph : program) {
            instructions.add(() -> "");
            instructions.addAll(instructionSelector.transform(graph));
        }

        LivenessAnalyzer livenessAnalyzer = new LivenessAnalyzer();
        Map<Integer, Set<InstructionTarget>> liveIn = livenessAnalyzer.execute(new LinkedList<>(instructions));


        return instructions.stream().map(Instruction::toCode).collect(Collectors.joining("\n"));
    }
}
