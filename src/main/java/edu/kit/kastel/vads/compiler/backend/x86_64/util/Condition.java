package edu.kit.kastel.vads.compiler.backend.x86_64.util;

public enum Condition {
    EQUALS("E"),
    NOT_EQUALS("NE"),
    LESS("L"),
    GREATER("G"),
    LESS_EQUALS("LE"),
    GREATER_EQUALS("GE");

    private final String code;

    Condition(String code) {
        this.code = code;
    }

    public String toCode() {
        return code;
    }
}
