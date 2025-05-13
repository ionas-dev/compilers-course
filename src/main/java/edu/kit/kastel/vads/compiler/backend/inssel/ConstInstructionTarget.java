package edu.kit.kastel.vads.compiler.backend.inssel;

// TODO: Extend for more constants
public record ConstInstructionTarget(int value) implements InstructionTarget {

    @Override
    public String toCode() {
        return "$" + value;
    }

    @Override
    public BitSize size() {
        return BitSize.BIT32;
    }
}
