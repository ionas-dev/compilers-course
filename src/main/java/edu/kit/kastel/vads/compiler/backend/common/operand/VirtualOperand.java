package edu.kit.kastel.vads.compiler.backend.common.operand;

import edu.kit.kastel.vads.compiler.backend.common.util.BitSize;

import java.util.Objects;

public record VirtualOperand(int id) implements Operand {

    @Override
    public String toCode(BitSize size) {
        return "%" + id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VirtualOperand that = (VirtualOperand) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
