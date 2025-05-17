package edu.kit.kastel.vads.compiler.backend.x86_64.statement;


public final class TextDirective implements X86Statement {

    @Override
    public String toCode() {
        return ".text";
    }
}
