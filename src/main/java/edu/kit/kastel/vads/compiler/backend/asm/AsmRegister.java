package edu.kit.kastel.vads.compiler.backend.asm;

import edu.kit.kastel.vads.compiler.backend.regalloc.Register;

public record AsmRegister(int id) implements Register {
    @Override
    public String toString() {
        return "%r" + id() + "d";
    }
}
