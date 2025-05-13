package edu.kit.kastel.vads.compiler.backend.x86_64.inssel;

import edu.kit.kastel.vads.compiler.backend.inssel.Instruction;
import edu.kit.kastel.vads.compiler.backend.inssel.InstructionTarget;

public interface BinaryOpInstruction extends Instruction {

    InstructionTarget source();
    InstructionTarget target();
}
