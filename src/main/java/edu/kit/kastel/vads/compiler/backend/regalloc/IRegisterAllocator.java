package edu.kit.kastel.vads.compiler.backend.regalloc;

import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.Node;

import java.util.Map;

public interface IRegisterAllocator {

    Map<Node, IRegister> allocateRegisters(IrGraph graph);
}
