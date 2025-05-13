package edu.kit.kastel.vads.compiler.backend.x86_64.regalloc;

import edu.kit.kastel.vads.compiler.backend.inssel.BitSize;
import edu.kit.kastel.vads.compiler.backend.regalloc.Register;

public class BaseAddressRegister extends Register {

    public BaseAddressRegister(BitSize size) {
        super(size);
    }

    @Override
    public String toCode() {
        return switch (super.size()) {
            case BIT8 -> "%dl";
            case BIT16 -> "%dx";
            case BIT32 -> "%edx";
            case BIT64 -> "%rdx";
        };
    }

    @Override
    public int hashCode() {
        return BaseAddressRegister.class.hashCode() + size().hashCode();
    }
}
