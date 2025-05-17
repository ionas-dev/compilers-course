package edu.kit.kastel.vads.compiler.backend.x86_64.operand;

import edu.kit.kastel.vads.compiler.backend.common.operand.PhysicalOperand;
import edu.kit.kastel.vads.compiler.backend.common.util.BitSize;

public record ImmediateOperand(int value) implements PhysicalOperand {

    @Override
    public String toCode(BitSize size) {
        return "$" + value;
    }

    @Override
    public int hashCode() {
        return ImmediateOperand.class.hashCode() + Integer.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass()) && this.hashCode() == obj.hashCode();
    }
}
