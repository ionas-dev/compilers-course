package edu.kit.kastel.vads.compiler.backend.x86_64.statement;


public final class Comment implements X86Statement {

    private final String comment;

    public Comment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toCode() {
        return "; " + comment;
    }
}
