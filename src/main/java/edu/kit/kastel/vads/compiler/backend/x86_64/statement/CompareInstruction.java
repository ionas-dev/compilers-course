package edu.kit.kastel.vads.compiler.backend.x86_64.statement;

import edu.kit.kastel.vads.compiler.backend.common.operand.Operand;
import edu.kit.kastel.vads.compiler.backend.common.util.BitSize;

public final class CompareInstruction implements X86Statement{

    private final Operand leftOperand;
    private final Operand rightOperand;

    private final BitSize size;

    public CompareInstruction(Operand leftOperand, Operand rightOperand, BitSize size) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.size = size;
    }

    @Override
    public String toCode() {
        return "cmp" + suffix() + leftOperand.toCode(size) + ", " + rightOperand.toCode(size);
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
