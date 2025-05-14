package edu.kit.kastel.vads.compiler.backend.inssel;

// TODO: Instruction bekommt size
@FunctionalInterface
public interface Instruction {

    String toCode();
}
