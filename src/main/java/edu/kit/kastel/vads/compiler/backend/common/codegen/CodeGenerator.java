package edu.kit.kastel.vads.compiler.backend.common.codegen;

import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.myir.node.ProgramNode;

import java.util.List;

public interface CodeGenerator {
    String generateCode(List<IrGraph> program) throws UnsupportedOperationException;

    String generateCodeByProgramNodes(List<ProgramNode> programs) throws UnsupportedOperationException;
}
