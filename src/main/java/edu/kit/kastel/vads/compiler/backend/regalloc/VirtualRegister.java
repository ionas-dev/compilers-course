package edu.kit.kastel.vads.compiler.backend.regalloc;

import edu.kit.kastel.vads.compiler.backend.inssel.BitSize;

public class VirtualRegister implements IRegister {
    private static int _id = 0;
    private final int id = _id++;

    @Override
    public String toCode(BitSize size) {
        return "%" + id;
    }


    @Override
    public int hashCode() {
        return VirtualRegister.class.hashCode() + Integer.hashCode(id);
    }
}
