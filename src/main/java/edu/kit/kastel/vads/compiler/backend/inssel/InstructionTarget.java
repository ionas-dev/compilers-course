package edu.kit.kastel.vads.compiler.backend.inssel;

public interface InstructionTarget {

    String toCode();

    BitSize size();
}
