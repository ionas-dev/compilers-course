package edu.kit.kastel.vads.compiler.backend.x86_64.statement;

import edu.kit.kastel.vads.compiler.backend.common.util.BitSize;
import edu.kit.kastel.vads.compiler.backend.common.operand.Operand;

public final class SignedMultiplyInstruction implements X86Statement {

    private Operand source;
    private final BitSize size;

    public SignedMultiplyInstruction(Operand source, BitSize size) {
        this.source = source;
        this.size = size;
    }

    public Operand source() {
        return source;
    }

    public BitSize size() {
        return size;
    }

    public void setSource(Operand source) {
        this.source = source;
    }

    @Override
    public String toCode() {
        return "imul" + suffix() + " " + source.toCode(size);
    }

    private String suffix() {
        return switch (size) {
            case BIT8 -> "b";
            case BIT16 -> "w";
            case BIT32 -> "l";
            case BIT64 -> "q";
        };
    }
}
