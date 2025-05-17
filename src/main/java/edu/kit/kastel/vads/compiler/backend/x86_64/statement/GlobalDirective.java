package edu.kit.kastel.vads.compiler.backend.x86_64.statement;


public final class GlobalDirective implements X86Statement {

    private final String directive;

    public GlobalDirective(String directive) {
        this.directive = directive;
    }

    @Override
    public String toCode() {
        return ".global " + directive;
    }
}
