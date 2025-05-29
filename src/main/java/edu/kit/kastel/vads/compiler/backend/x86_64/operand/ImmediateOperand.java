package edu.kit.kastel.vads.compiler.backend.x86_64.operand;

import edu.kit.kastel.vads.compiler.backend.common.operand.PhysicalOperand;
import edu.kit.kastel.vads.compiler.backend.common.util.BitSize;

import java.util.Objects;

public record ImmediateOperand(int value) implements PhysicalOperand {

    @Override
    public String toCode(BitSize size) {
        return "$" + value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ImmediateOperand that = (ImmediateOperand) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
