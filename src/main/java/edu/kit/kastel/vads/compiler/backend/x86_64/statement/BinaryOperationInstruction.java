package edu.kit.kastel.vads.compiler.backend.x86_64.statement;

import edu.kit.kastel.vads.compiler.backend.common.util.BitSize;
import edu.kit.kastel.vads.compiler.backend.common.operand.Operand;

public final class BinaryOperationInstruction implements X86Statement {

    private Operand source;
    private Operand target;
    private final Operation operation;
    private final BitSize size;

    public BinaryOperationInstruction(Operand source, Operand target, Operation operation, BitSize size) {
        this.source = source;
        this.target = target;
        this.operation = operation;
        this.size = size;
    }

    public Operand source() {
        return source;
    }

    public Operand target() {
        return target;
    }

    public Operation operation() {
        return operation;
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
    public String toCode() { return operation + suffix() + " " + source.toCode(size) + ", " + target.toCode(size); }

    private String suffix() {
        return switch (size) {
            case BIT8 -> "b";
            case BIT16 -> "w";
            case BIT32 -> "l";
            case BIT64 -> "q";
        };
    }

    public enum Operation {
        ADD("add"),
        SUB("sub");

        private final String value;

        Operation(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

}
