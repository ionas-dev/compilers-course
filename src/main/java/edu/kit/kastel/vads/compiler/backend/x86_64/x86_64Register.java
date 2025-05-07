package edu.kit.kastel.vads.compiler.backend.x86_64;

import edu.kit.kastel.vads.compiler.backend.regalloc.Register;

public record x86_64Register(int id) implements Register {
    @Override
    public String toString() {
        return "%r" + id() + "d";
    }
}
