package edu.kit.kastel.vads.compiler.backend.common.operand;

import edu.kit.kastel.vads.compiler.backend.common.util.BitSize;

public interface Operand {

    String toCode(BitSize size);
}
