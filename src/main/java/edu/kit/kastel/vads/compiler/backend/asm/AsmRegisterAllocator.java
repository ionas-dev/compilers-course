package edu.kit.kastel.vads.compiler.backend.asm;

import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.regalloc.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.Node;

import java.util.Map;

public class AsmRegisterAllocator implements RegisterAllocator {


    @Override
    public Map<Node, Register> allocateRegisters(IrGraph graph) {
        // TODO: Implement
        return Map.of();
    }
}
