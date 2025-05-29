package edu.kit.kastel.vads.compiler.backend.x86_64.statement;

public final class CallInstruction implements X86Statement {

    private final String name;

    public CallInstruction(String name) {
        this.name = name;
    }

    @Override
    public String toCode() {
        return "call " + name;
    }
}
