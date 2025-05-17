package edu.kit.kastel.vads.compiler.backend.x86_64.statement;


public final class ReturnInstruction implements X86Statement {

    @Override
    public String toCode() {
        return "ret";
    }
}
