package edu.kit.kastel.vads.compiler.backend.x86_64.inssel;

import edu.kit.kastel.vads.compiler.backend.inssel.InstructionTarget;

public record MulInstruction(InstructionTarget source, InstructionTarget target) implements BinaryOpInstruction {

    @Override
    public String toCode() {
        return "mul " + source.toCode() + ", " + target.toCode();
    }
}
