package edu.kit.kastel.vads.compiler.backend.codegen;

import edu.kit.kastel.vads.compiler.ir.IrGraph;

import java.util.List;

public interface ICodeGenerator {
    String generateCode(List<IrGraph> program);
}
