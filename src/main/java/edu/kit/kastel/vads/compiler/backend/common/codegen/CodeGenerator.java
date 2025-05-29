package edu.kit.kastel.vads.compiler.backend.common.codegen;

import edu.kit.kastel.vads.compiler.ir.IrGraph;

import java.util.List;

public interface CodeGenerator {
    String generateCode(List<IrGraph> program) throws UnsupportedOperationException;
}
