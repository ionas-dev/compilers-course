package edu.kit.kastel.vads.compiler.backend.x86_64.statement;

import edu.kit.kastel.vads.compiler.backend.common.operand.Operand;
import edu.kit.kastel.vads.compiler.backend.common.util.BitSize;

public final class MoveInstruction implements X86Statement {

    private Operand source;
    private Operand target;
    private final BitSize size;

    public MoveInstruction(Operand source, Operand target, BitSize size) {
        this.source = source;
        this.target = target;
        this.size = size;
    }

    public Operand source() {
        return source;
    }

    public Operand target() {
        return target;
    }

    public BitSize size() {
        return size;
    }

    public void setSource(Operand source) {
        this.source = source;
    }

    public void setTarget(Operand target) {
        this.target = target;
    }

    @Override
    public String toCode() {
        return "mov" + suffix() + " " + source.toCode(size) + ", " + target.toCode(size);
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
