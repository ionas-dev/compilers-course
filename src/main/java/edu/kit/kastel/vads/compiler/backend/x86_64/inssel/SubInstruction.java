package edu.kit.kastel.vads.compiler.backend.x86_64.inssel;

import edu.kit.kastel.vads.compiler.backend.inssel.InstructionTarget;

public record SubInstruction(InstructionTarget source, InstructionTarget target) implements BinaryOpInstruction {

    @Override
    public String toCode() {
        return "sub " + source.toCode() + ", " + target.toCode();
    }
}
