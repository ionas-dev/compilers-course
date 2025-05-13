package edu.kit.kastel.vads.compiler.backend.x86_64.inssel;

import edu.kit.kastel.vads.compiler.backend.inssel.InstructionTarget;

public record AddInstruction(InstructionTarget source, InstructionTarget target) implements BinaryOpInstruction {

    @Override
    public String toCode() {
        return "add " + source.toCode() + ", " + target.toCode();
    }
}
