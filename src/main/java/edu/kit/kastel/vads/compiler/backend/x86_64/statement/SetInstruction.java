package edu.kit.kastel.vads.compiler.backend.x86_64.statement;

import edu.kit.kastel.vads.compiler.backend.common.operand.Operand;
import edu.kit.kastel.vads.compiler.backend.common.util.BitSize;
import edu.kit.kastel.vads.compiler.backend.x86_64.util.Condition;

public final class SetInstruction implements X86Statement {

    private final Operand operand;
    private final Condition condition;
    private final BitSize size;

    public SetInstruction(Operand operand, Condition condition, BitSize size) {
        this.operand = operand;
        this.condition = condition;
        this.size = size;
    }

    @Override
    public String toCode() {
        return "set" + condition.toCode() + " " + operand.toCode(size);
    }
}
