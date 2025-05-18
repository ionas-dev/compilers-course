package edu.kit.kastel.vads.compiler.backend.x86_64.statement;

public final class Label implements X86Statement {

    private final String value;

    public Label(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toCode() {
        return value + ":";
    }
}
