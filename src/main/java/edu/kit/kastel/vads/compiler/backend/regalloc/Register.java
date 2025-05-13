package edu.kit.kastel.vads.compiler.backend.regalloc;

import edu.kit.kastel.vads.compiler.backend.inssel.BitSize;
import edu.kit.kastel.vads.compiler.backend.inssel.InstructionTarget;

// TODO: Change to interface
public abstract class Register implements InstructionTarget {
    private final BitSize size;

    public Register(BitSize size) {
        this.size = size;
    }

    public BitSize size() {
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass()) && this.hashCode() == obj.hashCode();
    }
}
