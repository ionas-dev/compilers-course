package edu.kit.kastel.vads.compiler.backend.x86_64.regalloc;

import edu.kit.kastel.vads.compiler.backend.inssel.BitSize;
import edu.kit.kastel.vads.compiler.backend.regalloc.IRegister;

public enum Register implements IRegister {
    ACCUMULATOR("rax", "eax", "ax", "al"),
    BASE("rbx", "ebx", "bx", "bl"),
    COUNTER("rcx", "ecx", "cx", "cl"),
    DATA("rdx", "edx", "dx", "dl"),
    SOURCE_INDEX("rsi", "esi", "si", "sil"),
    DESTINATION_INDEX("rdi", "edi", "di", "dil"),
    BASE_POINTER("rbp", "ebp", "bp", "bpl"),
    STACK_POINTER("rsp", "esp", "sp", "spl"),
    R8("r8", "r8d" , "r8w", "r8b"),
    R9("r9", "r9d" , "r9w", "r9b"),
    R10("r10", "r10d" , "r10w", "r10b"),
    R11("r11", "r11d" , "r11w", "r11b"),
    R12("r12", "r12d" , "r12w", "r12b"),
    R13("r13", "r13d" , "r13w", "r13b"),
    R14("r14", "r14d" , "r14w", "r14b"),
    R15("r15", "r15d" , "r15w", "r15b");

    private final String bit64;
    private final String bit32;
    private final String bit16;
    private final String bit8;

    Register(String bit64, String bit32, String bit16, String bit8) {
        this.bit64 = bit64;
        this.bit32 = bit32;
        this.bit16 = bit16;
        this.bit8 = bit8;
    }

    @Override
    public String toCode(BitSize size) {
        return "%" + switch (size) {
            case BIT64 -> bit64;
            case BIT32 -> bit32;
            case BIT16 -> bit16;
            case BIT8 -> bit8;
        };
    }
}
