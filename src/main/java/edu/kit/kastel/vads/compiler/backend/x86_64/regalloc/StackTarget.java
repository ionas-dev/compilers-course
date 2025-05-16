package edu.kit.kastel.vads.compiler.backend.x86_64.regalloc;

import edu.kit.kastel.vads.compiler.backend.inssel.BitSize;
import edu.kit.kastel.vads.compiler.backend.inssel.InstructionTarget;

public class StackTarget implements InstructionTarget {

    // TODO: Move into register allocator
    private static int _offset = 0;
    private final int offset;

    public StackTarget(BitSize size) {
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
}
