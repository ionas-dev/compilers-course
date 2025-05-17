package edu.kit.kastel.vads.compiler.backend.common.util;

public enum BitSize {
    BIT8(8),
    BIT16(16),
    BIT32(32),
    BIT64(64);

    private final int value;

    BitSize(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
