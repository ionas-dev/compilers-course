package edu.kit.kastel.vads.compiler.backend.x86_64.inssel;

import edu.kit.kastel.vads.compiler.backend.inssel.Instruction;
import edu.kit.kastel.vads.compiler.backend.inssel.InstructionTarget;
import edu.kit.kastel.vads.compiler.backend.inssel.BitSize;

public record MoveInstruction(InstructionTarget source, InstructionTarget target) implements Instruction {

    private String suffix() {
        BitSize size = source.size().value() > target.size().value() ? source.size() : target.size();

        return switch (size) {
            case BIT8 -> "b";
            case BIT16 -> "w";
            case BIT32 -> "l";
            case BIT64 -> "q";
        };
    }

    @Override
    public String toCode() {
        return "mov" + suffix() + " " + source.toCode() + ", " + target.toCode();
    }
}
