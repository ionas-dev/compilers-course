package edu.kit.kastel.vads.compiler.backend.x86_64.statement;

import edu.kit.kastel.vads.compiler.backend.x86_64.util.Condition;

public final class JumpIfInstruction implements X86Statement {

    private final String label;

    private final Condition condition;

    public JumpIfInstruction(String label, Condition condition) {
        this.label = label;
        this.condition = condition;
    }

    @Override
    public String toCode() {
        return "G" + condition.toCode() + " " + label;
    }
}
