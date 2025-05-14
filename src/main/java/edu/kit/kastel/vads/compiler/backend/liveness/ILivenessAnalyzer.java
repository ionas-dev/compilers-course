package edu.kit.kastel.vads.compiler.backend.liveness;

import edu.kit.kastel.vads.compiler.backend.inssel.Instruction;
import edu.kit.kastel.vads.compiler.backend.regalloc.IRegister;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ILivenessAnalyzer {

    Map<Integer, Set<IRegister>> computeLiveIn(List<Instruction> instructions);
}
