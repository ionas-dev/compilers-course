package edu.kit.kastel.vads.compiler.backend.x86_64.statement;

public final class JumpInstruction implements X86Statement {

    private final String label;

    public JumpInstruction(String label) {
        this.label = label;
    }

    @Override
    public String toCode() {
        return "jmp " + label;
    }
}
