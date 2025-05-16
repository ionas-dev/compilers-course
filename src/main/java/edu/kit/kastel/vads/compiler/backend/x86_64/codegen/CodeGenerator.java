package edu.kit.kastel.vads.compiler.backend.x86_64.codegen;

import edu.kit.kastel.vads.compiler.backend.codegen.ICodeGenerator;
import edu.kit.kastel.vads.compiler.backend.inssel.BitSize;
import edu.kit.kastel.vads.compiler.backend.inssel.Instruction;
import edu.kit.kastel.vads.compiler.backend.inssel.IInstructionSelector;
import edu.kit.kastel.vads.compiler.backend.regalloc.IRegister;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.BinaryOperationInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.InstructionSelector;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.MoveInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.x86_64.regalloc.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.IrGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CodeGenerator implements ICodeGenerator {

    @Override
    public String generateCode(List<IrGraph> program) throws UnsupportedOperationException {
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

        RegisterAllocator allocator = new RegisterAllocator();

        for (IrGraph graph : program) {
            instructions.add(() -> "");
            instructions.add(() -> "_" + graph.name() + ":");
            instructions.addAll(allocator.allocateRegisters(instructionSelector.transform(graph)));
        }

        return instructions.stream()
                .filter(instruction -> !(instruction instanceof MoveInstruction) || !((MoveInstruction) instruction).source().equals(((MoveInstruction) instruction).target()))
                .map(Instruction::toCode)
                .collect(Collectors.joining("\n"));
    }
}
