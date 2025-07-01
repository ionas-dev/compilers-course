package edu.kit.kastel.vads.compiler.backend.x86_64.statement;

import edu.kit.kastel.vads.compiler.backend.common.operand.Operand;
import edu.kit.kastel.vads.compiler.backend.common.util.BitSize;

public final class TestInstruction implements X86Statement {

    private final Operand leftOperand;
    private final Operand rightOperand;
    private final BitSize size;

    public TestInstruction(Operand leftOperand, Operand rightOperand, BitSize size) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.size = size;
    }


    @Override
    public String toCode() {
        return "test " + leftOperand.toCode(size) + ", " + rightOperand.toCode(size);
    }
}
