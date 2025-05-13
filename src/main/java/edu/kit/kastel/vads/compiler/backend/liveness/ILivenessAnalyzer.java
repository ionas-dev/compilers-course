package edu.kit.kastel.vads.compiler.backend.liveness;

import edu.kit.kastel.vads.compiler.backend.inssel.Instruction;
import edu.kit.kastel.vads.compiler.backend.inssel.InstructionTarget;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ILivenessAnalyzer {

    Map<Integer, Set<InstructionTarget>> execute(List<Instruction> instructions);
}
