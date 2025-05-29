package edu.kit.kastel.vads.compiler.backend.common.regalloc;

import edu.kit.kastel.vads.compiler.backend.common.operand.Operand;
import edu.kit.kastel.vads.compiler.backend.common.util.graph.Graph;

import java.util.Map;
import java.util.Set;

public final class ChordalGraph {

    /**
     * Builds a chordal graph
     * @param liveIn A map from a line to operands which are live coming into this line
     * @return A chordal graph of the operands
     */
    public static Graph<Operand> buildGraph(Map<Integer, Set<Operand>> liveIn) {
        Graph<Operand> graph = new Graph<>();

        // TODO: Make more efficient
        for (Set<Operand> registers : liveIn.values()) {
            for (Operand register : registers) {
                graph.add(register);
                for (Operand otherRegister : registers) {
                    if (register.equals(otherRegister)) {
                        continue;
                    }
                    graph.addEdge(register, otherRegister);
                }
            }
        }

        return graph;
    }
}
