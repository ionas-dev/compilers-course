package edu.kit.kastel.vads.compiler.backend.x86_64.inssel;

import edu.kit.kastel.vads.compiler.backend.inssel.Instruction;

public record ReturnInstruction() implements Instruction {

    @Override
    public String toCode() {
        return "ret";
    }
}
