package edu.kit.kastel.vads.compiler.backend.x86_64.inssel;

import edu.kit.kastel.vads.compiler.backend.inssel.Instruction;
import edu.kit.kastel.vads.compiler.backend.inssel.InstructionTarget;
import edu.kit.kastel.vads.compiler.backend.inssel.BitSize;
import edu.kit.kastel.vads.compiler.backend.regalloc.IRegister;

public record MoveInstruction(InstructionTarget source, InstructionTarget target, BitSize size) implements Instruction {

    private String suffix() {
        return switch (size) {
            case BIT8 -> "b";
            case BIT16 -> "w";
            case BIT32 -> "l";
            case BIT64 -> "q";
        };
    }

    @Override
    public String toCode() {
        return "mov" + suffix() + " " + source.toCode(size) + ", " + target.toCode(size);
    }
}
