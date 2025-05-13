package edu.kit.kastel.vads.compiler.backend.inssel;

import edu.kit.kastel.vads.compiler.ir.IrGraph;

import java.util.List;

public interface IInstructionSelector {

    List<Instruction> transform(IrGraph irGraph);
}
