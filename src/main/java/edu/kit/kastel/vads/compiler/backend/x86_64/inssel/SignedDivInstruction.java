package edu.kit.kastel.vads.compiler.backend.x86_64.inssel;

import edu.kit.kastel.vads.compiler.backend.inssel.Instruction;
import edu.kit.kastel.vads.compiler.backend.inssel.InstructionTarget;

public record SignedDivInstruction(InstructionTarget source) implements Instruction {

    @Override
    public String toCode() {
        return "idivl " + source.toCode();
    }
}
