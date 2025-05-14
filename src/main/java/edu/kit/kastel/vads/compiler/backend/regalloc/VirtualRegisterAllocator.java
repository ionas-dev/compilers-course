package edu.kit.kastel.vads.compiler.backend.regalloc;

import edu.kit.kastel.vads.compiler.backend.inssel.BitSize;
import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.Block;
import edu.kit.kastel.vads.compiler.ir.node.Node;
import edu.kit.kastel.vads.compiler.ir.node.ProjNode;
import edu.kit.kastel.vads.compiler.ir.node.ReturnNode;
import edu.kit.kastel.vads.compiler.ir.node.StartNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VirtualRegisterAllocator implements IRegisterAllocator {
    private final Map<Node, IRegister> registers = new HashMap<>();

    @Override
    public Map<Node, IRegister> allocateRegisters(IrGraph graph) {
        Set<Node> visited = new HashSet<>();
        visited.add(graph.endBlock());
        scan(graph.endBlock(), visited);
        return Map.copyOf(this.registers);
    }

    private void scan(Node node, Set<Node> visited) {
        for (Node predecessor : node.predecessors()) {
            if (visited.add(predecessor)) {
                scan(predecessor, visited);
            }
        }

        if (needsRegister(node)) {
            // TODO: Size richtig setzen
            this.registers.put(node, new VirtualRegister());
        }
    }

    private static boolean needsRegister(Node node) {
        return !(node instanceof ProjNode || node instanceof StartNode || node instanceof Block || node instanceof ReturnNode);
    }
}
