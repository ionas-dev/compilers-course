package edu.kit.kastel.vads.compiler.backend.x86_64.statement;

public final class Label implements X86Statement {

    private final String label;

    public Label(String label) {
        this.label = label;
    }

    @Override
    public String toCode() {
        return label + ":";
    }
}
