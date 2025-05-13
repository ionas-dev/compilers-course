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

    @Override
    public int hashCode() {
        return ConstInstructionTarget.class.hashCode() + Integer.hashCode(value) + size().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass()) && this.hashCode() == obj.hashCode();
    }
}
