package edu.kit.kastel.vads.compiler.backend.inssel;

// TODO: Extend for more constants
public record ConstInstructionTarget(int value) implements InstructionTarget {

    @Override
    public String toCode(BitSize size) {
        return "$" + value;
    }

    @Override
    public int hashCode() {
        return ConstInstructionTarget.class.hashCode() + Integer.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass()) && this.hashCode() == obj.hashCode();
    }
}
