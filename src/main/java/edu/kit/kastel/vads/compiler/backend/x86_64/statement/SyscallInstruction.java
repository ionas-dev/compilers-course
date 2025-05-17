package edu.kit.kastel.vads.compiler.backend.x86_64.statement;

public final class SyscallInstruction implements X86Statement {
    @Override
    public String toCode() {
        return "syscall";
    }
}
