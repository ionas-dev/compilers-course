package edu.kit.kastel.vads.compiler.backend.x86_64.inssel;

import edu.kit.kastel.vads.compiler.backend.inssel.BitSize;
import edu.kit.kastel.vads.compiler.backend.inssel.Instruction;
import edu.kit.kastel.vads.compiler.backend.inssel.InstructionTarget;

public record MultiplyInstruction(InstructionTarget source, BitSize size) implements Instruction {
    @Override
    public String toCode() {
        return "imul" + suffix() + " " + source.toCode(size);
    }

    private String suffix() {
        return switch (size) {
            case BIT8 -> "b";
            case BIT16 -> "w";
            case BIT32 -> "l";
            case BIT64 -> "q";
        };
    }
}
