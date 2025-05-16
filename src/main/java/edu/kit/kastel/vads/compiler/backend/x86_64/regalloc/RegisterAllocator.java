package edu.kit.kastel.vads.compiler.backend.x86_64.regalloc;

import edu.kit.kastel.vads.compiler.backend.inssel.BitSize;
import edu.kit.kastel.vads.compiler.backend.inssel.Instruction;
import edu.kit.kastel.vads.compiler.backend.inssel.InstructionTarget;
import edu.kit.kastel.vads.compiler.backend.regalloc.IRegister;
import edu.kit.kastel.vads.compiler.backend.regalloc.VirtualRegister;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.BinaryOperationInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.MoveInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.MultiplyInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.inssel.SignedDivisionInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.liveness.LivenessAnalyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

public class RegisterAllocator {

    Map<Integer, Register> registers = new HashMap<>();
    EnumSet<Register> availableRegisters = EnumSet.complementOf(EnumSet.of(Register.STACK_POINTER, Register.BASE_POINTER));

    public List<Instruction> allocateRegisters(List<Instruction> instructions) {
        Graph<IRegister> graph = buildChordalGraph(instructions);
        List<Node<IRegister>> simplicialEliminationOrderedList = order(graph.nodes());
        Map<IRegister, Integer> coloredNodes = colorNodes(simplicialEliminationOrderedList);

        return exchangeVirtualRegister(instructions, coloredNodes);
    }


    private List<Instruction> exchangeVirtualRegister(List<Instruction> instructions, Map<IRegister, Integer> coloredNodes) {
        List<Instruction> validInstructions = new ArrayList<>(instructions.size());

        for (Instruction instruction : instructions) {
            switch (instruction) {
                case BinaryOperationInstruction binaryOperationInstruction -> {
                    InstructionTarget source = binaryOperationInstruction.source() instanceof VirtualRegister
                            ? findRealRegister((VirtualRegister) binaryOperationInstruction.source(), coloredNodes)
                            : binaryOperationInstruction.source();
                    IRegister target = binaryOperationInstruction.target() instanceof VirtualRegister
                            ? findRealRegister((VirtualRegister) binaryOperationInstruction.target(), coloredNodes)
                            : binaryOperationInstruction.target();

                    validInstructions.add(new BinaryOperationInstruction(source, target, binaryOperationInstruction.operation(), binaryOperationInstruction.size()));
                }
                case MoveInstruction moveInstruction -> {
                    InstructionTarget source = moveInstruction.source() instanceof VirtualRegister
                            ? findRealRegister((VirtualRegister) moveInstruction.source(), coloredNodes)
                            : moveInstruction.source();
                    IRegister target = moveInstruction.target() instanceof VirtualRegister
                            ? findRealRegister((VirtualRegister) moveInstruction.target(), coloredNodes)
                            : moveInstruction.target();

                    validInstructions.add(new MoveInstruction(source, target, moveInstruction.size()));
                }
                case SignedDivisionInstruction(VirtualRegister source, BitSize size) -> {
                    IRegister register = findRealRegister(source, coloredNodes);

                    validInstructions.add(new SignedDivisionInstruction(register, size));
                }
                case MultiplyInstruction(VirtualRegister source, BitSize size) -> {
                    IRegister register = findRealRegister(source, coloredNodes);

                    validInstructions.add(new MultiplyInstruction(register, size));
                }
                default -> validInstructions.add(instruction);
            }
        }
        return validInstructions;
    }

    private Register findRealRegister(VirtualRegister register, Map<IRegister, Integer> coloredNodes) {
        Integer color = coloredNodes.get(register);
        if (color != null) {
            assert registers.containsKey(color) : "Implementation error";
            return registers.get(color);
        } else if (registers.isEmpty() && availableRegisters.stream().findFirst().isPresent()) {
            return availableRegisters.stream().findFirst().get();
        }
        return registers.get(0);
    }

    private Map<IRegister, Integer> colorNodes(List<Node<IRegister>> nodes) {
        Map<IRegister, Integer> coloredNodes = new HashMap<>();
        for (Node<IRegister> node : nodes) {
            int color = node.getNeighbors().stream()
                    .map(neighbor -> Optional.ofNullable(coloredNodes.get(neighbor.getValue())).orElse(-1))
                    .mapToInt(Integer::intValue).max().orElse(-1);
            coloredNodes.put(node.getValue(), color + 1);
            if (node.getValue() instanceof Register) {
                registers.put(color + 1, (Register) node.getValue());
                availableRegisters.remove((Register) node.getValue());
            }
        }

        for (Node<IRegister> node : nodes) {
            Integer color = coloredNodes.get(node.getValue());
            if (!registers.containsKey(color)) {
                // TODO: Register spilling fixen
                Register availableRegister = availableRegisters.stream().findFirst().get();
                registers.put(color, availableRegister);
                availableRegisters.remove(availableRegister);
            }
        }

        return coloredNodes;
    }

    private static Graph<IRegister> buildChordalGraph(List<Instruction> instructions) {
        LivenessAnalyzer livenessAnalyzer = new LivenessAnalyzer();
        Map<Integer, Set<IRegister>> liveIn = livenessAnalyzer.computeLiveIn(instructions);

        Graph<IRegister> graph = new Graph<>();

        // TODO: Make more efficient
        for (Set<IRegister> registers : liveIn.values()) {
            for (IRegister register : registers) {
                graph.add(register);
                for (IRegister otherRegister : registers) {
                    if (register.equals(otherRegister)) {
                        continue;
                    }
                    graph.addEdge(register, otherRegister);
                }
            }
        }

        return graph;
    }


    /**
     * Algorithm: Maximum cardinality search
     * @return A simplicial elimination ordered list of nodes
     */
    private static List<Node<IRegister>> order(Collection<Node<IRegister>> nodes) {
        List<Node<IRegister>> orderedNodes = new ArrayList<>(nodes.size());

        PriorityQueue<Tuple> pq = new PriorityQueue<>(nodes.size(), Comparator.reverseOrder());
        nodes.stream().map(Tuple::new).forEach(pq::add);

        for (int i = 0; i < nodes.size(); i++) {
            Node<IRegister> maxNode = Objects.requireNonNull(pq.poll()).node;
            for (Node<IRegister> neighbor: maxNode.getNeighbors()) {
                pq.stream().filter(tuple -> tuple.node.equals(neighbor)).forEach(tuple -> tuple.value++);
            }
            orderedNodes.add(maxNode);
        }
        return orderedNodes;
    }

    private static class Tuple implements Comparable<Tuple> {

        private final Node<IRegister> node;
        private int value;

        public Tuple(Node<IRegister> node) {
            this.node = node;
            this.value = 0;
        }

        @Override
        public int compareTo(Tuple o) {
            return value - o.value;
        }
    }
}
