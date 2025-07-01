package edu.kit.kastel.vads.compiler.backend.x86_64.registerallocation;

import edu.kit.kastel.vads.compiler.backend.common.operand.Operand;
import edu.kit.kastel.vads.compiler.backend.common.operand.PhysicalOperand;
import edu.kit.kastel.vads.compiler.backend.common.operand.VirtualOperand;
import edu.kit.kastel.vads.compiler.backend.common.regalloc.ChordalGraph;
import edu.kit.kastel.vads.compiler.backend.common.util.BitSize;
import edu.kit.kastel.vads.compiler.backend.common.util.graph.Graph;
import edu.kit.kastel.vads.compiler.backend.common.util.graph.Node;
import edu.kit.kastel.vads.compiler.backend.x86_64.operand.ImmediateOperand;
import edu.kit.kastel.vads.compiler.backend.x86_64.operand.Register;
import edu.kit.kastel.vads.compiler.backend.x86_64.operand.StackSlotOperand;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.BinaryOperationInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.CallInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.Comment;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.EmptyStatement;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.GlobalDirective;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.Label;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.MoveInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.ReturnInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.SignExtendInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.SignedDivisionInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.SignedMultiplyInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.SyscallInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.TextDirective;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.X86Statement;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RegisterAllocator {

    private final Map<Integer, PhysicalOperand> physicalOperands = new HashMap<>();
    private final EnumSet<Register> availableRegisters = EnumSet.complementOf(
            EnumSet.of(Register.STACK_POINTER, Register.BASE_POINTER, Register.R11, Register.R12)
    );
    private final Register stackRegister1 = Register.R11;
    private final Register stackRegister2 = Register.R12;

    public List<X86Statement> allocateRegisters(List<X86Statement> statements) {
        Map<Integer, Set<Operand>> liveIn = new LivenessAnalyzer().computeLiveIn(statements);
        Graph<Operand> graph = ChordalGraph.buildGraph(liveIn);
        List<Node<Operand>> orderedNodes = graph.simplicialEliminationOrder();

        List<Node<Operand>> orderedNodes2 = graph.simplicialEliminationOrder2();
        Map<Operand, Integer> coloredNodes = color(orderedNodes2.reversed());

        return exchangeVirtualRegisters(statements, coloredNodes);
    }

    private List<X86Statement> exchangeVirtualRegisters(List<X86Statement> statements, Map<Operand, Integer> coloredNodes) {
        List<X86Statement> validStatements = new ArrayList<>(statements.size());

        for (X86Statement statement : statements) {
            switch (statement) {
                case BinaryOperationInstruction binaryOperationInstruction -> {
                    Operand source = binaryOperationInstruction.source() instanceof VirtualOperand
                            ? findPhysicalOperand((VirtualOperand) binaryOperationInstruction.source(), coloredNodes)
                            : binaryOperationInstruction.source();
                    Operand target = binaryOperationInstruction.target() instanceof VirtualOperand
                            ? findPhysicalOperand((VirtualOperand) binaryOperationInstruction.target(), coloredNodes)
                            : binaryOperationInstruction.target();

                    Operand tempSource = moveStackSlotToRegister(source, stackRegister1, validStatements, binaryOperationInstruction.size());
                    Operand tempTarget = moveStackSlotToRegister(target, stackRegister2, validStatements, binaryOperationInstruction.size());
                    validStatements.add(new BinaryOperationInstruction(tempSource, tempTarget, binaryOperationInstruction.operation(), binaryOperationInstruction.size()));
                    moveRegisterToStackSlot(stackRegister1, source, validStatements, binaryOperationInstruction.size());
                    moveRegisterToStackSlot(stackRegister2, target, validStatements, binaryOperationInstruction.size());
                }
                case MoveInstruction moveInstruction -> {
                    Operand source = moveInstruction.source() instanceof VirtualOperand
                            ? findPhysicalOperand((VirtualOperand) moveInstruction.source(), coloredNodes)
                            : moveInstruction.source();
                    Operand target = moveInstruction.target() instanceof VirtualOperand
                            ? findPhysicalOperand((VirtualOperand) moveInstruction.target(), coloredNodes)
                            : moveInstruction.target();

                    Operand tempSource = target instanceof StackSlotOperand ? moveStackSlotToRegister(source, stackRegister1, validStatements, moveInstruction.size()) : source;
                    validStatements.add(new MoveInstruction(tempSource, target, moveInstruction.size()));
                }
                case SignedDivisionInstruction signedDivisionInstruction
                        when signedDivisionInstruction.source() instanceof VirtualOperand virtualOperand -> {
                    Operand operand = findPhysicalOperand(virtualOperand, coloredNodes);

                    Operand tempOperand = moveStackSlotToRegister(operand, stackRegister1, validStatements, signedDivisionInstruction.size());
                    validStatements.add(new SignedDivisionInstruction(tempOperand, signedDivisionInstruction.size()));
                }
                case SignedMultiplyInstruction signedMultiplyInstruction
                        when signedMultiplyInstruction.source() instanceof VirtualOperand virtualOperand -> {
                    Operand operand = findPhysicalOperand(virtualOperand, coloredNodes);

                    Operand tempRegister = moveStackSlotToRegister(operand, stackRegister1, validStatements, signedMultiplyInstruction.size());
                    validStatements.add(new SignedMultiplyInstruction(tempRegister, signedMultiplyInstruction.size()));
                }
                case SyscallInstruction _ -> {
                    validStatements.add(statement);
                }
                case Label label -> {
                    validStatements.add(statement);
                    if (label.value().equals("_main") && StackSlotOperand.absoluteOffset() > 0) {
                        int reservationBytes = StackSlotOperand.absoluteOffset() + 16 - StackSlotOperand.absoluteOffset() % 16;
                        ImmediateOperand immediateOperand = new ImmediateOperand(reservationBytes);
                        validStatements.add(new BinaryOperationInstruction(immediateOperand, Register.STACK_POINTER, BinaryOperationInstruction.Operation.SUB, BitSize.BIT64));
                    }
                }
                case ReturnInstruction _ -> {
                    if (StackSlotOperand.absoluteOffset() > 0) {
                        int reservationBytes = StackSlotOperand.absoluteOffset() + 16 - StackSlotOperand.absoluteOffset() % 16;
                        ImmediateOperand immediateOperand = new ImmediateOperand(reservationBytes);
                        validStatements.add(new BinaryOperationInstruction(immediateOperand, Register.STACK_POINTER, BinaryOperationInstruction.Operation.ADD, BitSize.BIT64));
                    }
                        validStatements.add(statement);
                }
                case CallInstruction _, Comment _, EmptyStatement _, GlobalDirective _ , SignExtendInstruction _, SignedDivisionInstruction _, SignedMultiplyInstruction _, TextDirective _ -> validStatements.add(statement);
                default -> throw new IllegalStateException("Unexpected state: " + statement);
            }
        }

        return validStatements;
    }

    /**
     * Moves the value of an operand to a register if the operand is a stack slot
     * @param operand Operand to check, if it is a stack slot
     * @param register Register to move the value to
     * @param instructions List of instructions to add the move instruction
     * @param size Bit-Size of the operand-value
     * @return The input operand if it was no stack slot, otherwise the register the value was moved to
     */
    private Operand moveStackSlotToRegister(Operand operand, Register register, List<X86Statement> instructions, BitSize size) {
        if (operand instanceof StackSlotOperand) {
            instructions.add(new MoveInstruction(operand, register, size));
            return register;
        }
        return operand;
    }

    /**
     * Moves the value of an register to a stack slot if the operand is a stack slot
     * @param register Register to take the value from
     * @param operand Operand to check, if it is a stack slot
     * @param instructions List of instructions to add the move instruction
     * @param size Bit-Size of the operand-value
     */
    private void moveRegisterToStackSlot(Register register, Operand operand, List<X86Statement> instructions, BitSize size) {
        if (operand instanceof StackSlotOperand) {
            instructions.add(new MoveInstruction(register, operand, size));
        }
    }

    private Operand findPhysicalOperand(VirtualOperand virtualOperand, Map<Operand, Integer> coloredNodes) {
        Integer color = coloredNodes.get(virtualOperand);
        if (color != null) {
            return physicalOperands.get(color);
        } else if (physicalOperands.isEmpty() && availableRegisters.stream().findFirst().isPresent()) {
            return availableRegisters.stream().findFirst().get();
        }
        return physicalOperands.get(0);
    }

    private Map<Operand, Integer> color(List<Node<Operand>> nodes) {
        Map<Operand, Integer> coloredNodes = new HashMap<>();
        for (Node<Operand> node : nodes) {
            Set<Integer> usedColors = node.getNeighbors().stream()
                    .map(n -> coloredNodes.get(n.getValue()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            int color = 0;
            while (usedColors.contains(color)) {
                color++;
            }

            coloredNodes.put(node.getValue(), color);
            if (node.getValue() instanceof Register register) {
                physicalOperands.put(color, register);
                availableRegisters.remove(register);
            }
        }

        for (Node<Operand> node : nodes) {
            Integer color = coloredNodes.get(node.getValue());
            if (physicalOperands.containsKey(color)) {
                continue;
            }

            Optional<Register> availableRegister = availableRegisters.stream().findFirst();
            if (availableRegister.isPresent()) {
                availableRegisters.remove(availableRegister.get());
                physicalOperands.put(color, availableRegister.get());
            } else {
                physicalOperands.put(color, new StackSlotOperand(BitSize.BIT32));
            }
        }

        return coloredNodes;
    }
}
