package edu.kit.kastel.vads.compiler.backend.x86_64.inssel;

import edu.kit.kastel.vads.compiler.backend.inssel.BitSize;
import edu.kit.kastel.vads.compiler.backend.inssel.Instruction;
import edu.kit.kastel.vads.compiler.backend.inssel.InstructionTarget;
import edu.kit.kastel.vads.compiler.backend.regalloc.IRegister;

public record BinaryOperationInstruction(InstructionTarget source, IRegister target, Operation operation, BitSize size) implements Instruction {
    @Override
    public String toCode() {
        return operation + " " + source.toCode(size) + ", " + target.toCode(size);
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
