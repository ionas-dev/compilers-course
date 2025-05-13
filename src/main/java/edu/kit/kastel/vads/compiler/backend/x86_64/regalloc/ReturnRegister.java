package edu.kit.kastel.vads.compiler.backend.x86_64.regalloc;

import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.inssel.BitSize;


// TODO: Maybe change register to enums for differnet sizes
public class ReturnRegister extends Register {

    public ReturnRegister(BitSize size) {
        super(size);
    }

    @Override
    public String toCode() {
        return switch (super.size()) {
            case BIT8 -> "%al";
            case BIT16 -> "%ax";
            case BIT32 -> "%eax";
            case BIT64 -> "%rax";
        };
    }

    @Override
    public int hashCode() {
        return ReturnRegister.class.hashCode() + size().hashCode();
    }
}
