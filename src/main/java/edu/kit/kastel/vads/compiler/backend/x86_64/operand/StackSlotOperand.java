package edu.kit.kastel.vads.compiler.backend.x86_64.operand;

import edu.kit.kastel.vads.compiler.backend.common.operand.PhysicalOperand;
import edu.kit.kastel.vads.compiler.backend.common.util.BitSize;
import edu.kit.kastel.vads.compiler.backend.common.operand.Operand;

import java.util.Objects;

public class StackSlotOperand implements PhysicalOperand {

    // TODO: Move into register allocator
    private static int _offset = 0;
    private final int offset;

    public StackSlotOperand(BitSize size) {
        this.offset = _offset + size.value() / 8;
        _offset = this.offset;
    }

    public int offset() {
        return offset;
    }

    public static int absoluteOffset() {
        return _offset;
    }

    @Override
    public String toCode(BitSize size) {
        return offset + "(%rsp)";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StackSlotOperand that = (StackSlotOperand) o;
        return offset == that.offset;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(offset);
    }
}
