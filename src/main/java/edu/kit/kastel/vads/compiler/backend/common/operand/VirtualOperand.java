package edu.kit.kastel.vads.compiler.backend.common.operand;

import edu.kit.kastel.vads.compiler.backend.common.util.BitSize;

public record VirtualOperand(int id) implements Operand {

    @Override
    public String toCode(BitSize size) {
        return "%" + id;
    }


    @Override
    public int hashCode() {
        return VirtualOperand.class.hashCode() + Integer.hashCode(id);
    }
}
