package edu.kit.kastel.vads.compiler.backend.common.regalloc;

import edu.kit.kastel.vads.compiler.backend.common.operand.Operand;
import edu.kit.kastel.vads.compiler.backend.common.operand.VirtualOperand;
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

public class VirtualOperandAllocator {
    private final Map<Node, Operand> virtualOperands = new HashMap<>();
    private int operandCount = 0;

    public Map<Node, Operand> allocateVirtualOperands(IrGraph graph) {
        Set<Node> visited = new HashSet<>();
        visited.add(graph.endBlock());
        scan(graph.endBlock(), visited);
        return Map.copyOf(this.virtualOperands);
    }

    private void scan(Node node, Set<Node> visited) {
        for (Node predecessor : node.predecessors()) {
            if (visited.add(predecessor)) {
                scan(predecessor, visited);
            }
        }

        if (needsOperand(node)) {
            this.virtualOperands.computeIfAbsent(node, _ -> new VirtualOperand(operandCount++));
        }
    }

    private static boolean needsOperand(Node node) {
        return !(node instanceof ProjNode || node instanceof StartNode || node instanceof Block || node instanceof ReturnNode);
    }
}
