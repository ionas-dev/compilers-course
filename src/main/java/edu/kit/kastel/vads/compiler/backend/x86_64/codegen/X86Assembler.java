package edu.kit.kastel.vads.compiler.backend.x86_64.codegen;

import edu.kit.kastel.vads.compiler.backend.common.codegen.CodeGenerator;
import edu.kit.kastel.vads.compiler.backend.common.operand.Operand;
import edu.kit.kastel.vads.compiler.backend.common.regalloc.VirtualOperandAllocator;
import edu.kit.kastel.vads.compiler.backend.common.statement.Statement;
import edu.kit.kastel.vads.compiler.backend.common.util.BitSize;
import edu.kit.kastel.vads.compiler.backend.x86_64.instructionsel.InstructionSelector;
import edu.kit.kastel.vads.compiler.backend.x86_64.operand.ImmediateOperand;
import edu.kit.kastel.vads.compiler.backend.x86_64.operand.Register;
import edu.kit.kastel.vads.compiler.backend.x86_64.regalloc.RegisterAllocator;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.CallInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.EmptyStatement;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.GlobalDirective;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.Label;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.MoveInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.SyscallInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.TextDirective;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.X86Statement;
import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class X86Assembler implements CodeGenerator {

    private final RegisterAllocator registerAllocator = new RegisterAllocator();
    private final VirtualOperandAllocator virtualAllocator = new VirtualOperandAllocator();

    @Override
    public String generateCode(List<IrGraph> program) throws UnsupportedOperationException {
        List<X86Statement> instructions = new ArrayList<>(List.of(
                new GlobalDirective("main"),
                new GlobalDirective("_main"),
                new TextDirective(),
                new EmptyStatement(),
                new Label("main"),
                new CallInstruction("_main"),
                new EmptyStatement(),
                new MoveInstruction(Register.ACCUMULATOR, Register.DESTINATION_INDEX, BitSize.BIT64),
                new MoveInstruction(new ImmediateOperand(0x3c), Register.ACCUMULATOR, BitSize.BIT64),
                new SyscallInstruction())
        );

        for (IrGraph graph : program) {
            instructions.add(new EmptyStatement());
            instructions.add(new Label("_" + graph.name()));

            Map<Node, Operand> virtualOperands = virtualAllocator.allocateVirtualOperands(graph);
            instructions.addAll(InstructionSelector.selectInstructions(graph, virtualOperands));
        }

        return registerAllocator.allocateRegisters(instructions).stream()
                .filter(instruction -> !(instruction instanceof MoveInstruction) || !((MoveInstruction) instruction).source().equals(((MoveInstruction) instruction).target()))
                .map(Statement::toCode)
                .collect(Collectors.joining("\n"));
    }
}
