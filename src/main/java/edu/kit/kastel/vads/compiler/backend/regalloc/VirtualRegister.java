package edu.kit.kastel.vads.compiler.backend.regalloc;

import edu.kit.kastel.vads.compiler.backend.inssel.BitSize;

public class VirtualRegister extends Register {
    private static int _id = 0;
    private int id;

    public VirtualRegister(BitSize size) {
        super(size);
        id = _id++;
    }

    @Override
    public String toCode() {
        return "%" + id;
    }

    @Override
    public int hashCode() {
        return VirtualRegister.class.hashCode() + Integer.hashCode(id);
    }
}
