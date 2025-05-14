package edu.kit.kastel.vads.compiler.backend.x86_64.inssel;

import edu.kit.kastel.vads.compiler.backend.inssel.BitSize;
import edu.kit.kastel.vads.compiler.backend.inssel.Instruction;
import edu.kit.kastel.vads.compiler.backend.inssel.InstructionTarget;

public record SignedDivisionInstruction(InstructionTarget source, BitSize size) implements Instruction {

    @Override
    public String toCode() {
        return "idivl " + source.toCode(size);
    }
}
