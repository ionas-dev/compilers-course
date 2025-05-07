package edu.kit.kastel.vads.compiler.backend.x86_64;

import edu.kit.kastel.vads.compiler.backend.codegen.CodeGenerator;
import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.regalloc.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.AddNode;
import edu.kit.kastel.vads.compiler.ir.node.BinaryOperationNode;
import edu.kit.kastel.vads.compiler.ir.node.Block;
import edu.kit.kastel.vads.compiler.ir.node.ConstIntNode;
import edu.kit.kastel.vads.compiler.ir.node.DivNode;
import edu.kit.kastel.vads.compiler.ir.node.ModNode;
import edu.kit.kastel.vads.compiler.ir.node.MulNode;
import edu.kit.kastel.vads.compiler.ir.node.Node;
import edu.kit.kastel.vads.compiler.ir.node.Phi;
import edu.kit.kastel.vads.compiler.ir.node.ProjNode;
import edu.kit.kastel.vads.compiler.ir.node.ReturnNode;
import edu.kit.kastel.vads.compiler.ir.node.StartNode;
import edu.kit.kastel.vads.compiler.ir.node.SubNode;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import static edu.kit.kastel.vads.compiler.ir.util.NodeSupport.predecessorSkipProj;

public class x86_64CodeGenerator implements CodeGenerator {

    public String generateCode(List<IrGraph> program) {
        StringBuilder builder = new StringBuilder();
        generateAsmTemplate(builder);

        for (IrGraph graph : program) {
            RegisterAllocator allocator = new x86_64RegisterAllocator();
            Map<Node, Register> registers = allocator.allocateRegisters(graph);
            builder.repeat("\n", 2).append("_")
                    .append(graph.name())
                    .append(":\n");
            generateForGraph(graph, builder, registers);
        }

        return builder.toString();
    }

    private void generateAsmTemplate(StringBuilder builder) {
        StringJoiner asmTemplateBuilder = new StringJoiner(System.lineSeparator());
        asmTemplateBuilder.add(".global main")
                .add(".global _main")
                .add("")
                .add(".text")
                .add("main:")
                .add("call _main")
                .add("")
                .add("movq %rax, %rdi")
                .add("movq $0x3C, %rax")
                .add("syscall");
        builder.append(asmTemplateBuilder.toString());
    }

    private void generateForGraph(IrGraph graph, StringBuilder builder, Map<Node, Register> registers) {
        Set<Node> visited = new HashSet<>();
        scan(graph.endBlock(), visited, builder, registers);
    }

    private void scan(Node node, Set<Node> visited, StringBuilder builder, Map<Node, Register> registers) {
        for (Node predecessor : node.predecessors()) {
            if (visited.add(predecessor)) {
                scan(predecessor, visited, builder, registers);
            }
        }

        switch (node) {
            case AddNode addNode -> generateAddCode(builder, registers, addNode);
            case SubNode sub -> binary(builder, registers, sub, "sub");
            case MulNode mul -> binary(builder, registers, mul, "mul");
            case DivNode div -> binary(builder, registers, div, "div");
            case ModNode mod -> binary(builder, registers, mod, "mod");
            case ReturnNode r -> builder.repeat(" ", 2).append("movl ")
                .append(registers.get(predecessorSkipProj(r, ReturnNode.RESULT)))
                .append(", ")
                .append("%eax\n")
                .append("  ret");
            case ConstIntNode c -> builder.repeat(" ", 2)
                .append("movl $")
                .append(c.value())
                .append(", ")
                .append(registers.get(c));
            case Phi _ -> throw new UnsupportedOperationException("phi");
            case Block _, ProjNode _, StartNode _ -> {
                // do nothing, skip line break
                return;
            }
        }
        builder.append("\n");
    }

    private void generateAddCode(
            StringBuilder builder,
            Map<Node, Register> registers,
            BinaryOperationNode node
    ) {
        builder.repeat(" ", 2)
                .append("movl ")
                .append(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)))
                .append(", ")
                .append(registers.get(node))
                .append("\n")
                .repeat(" ", 2)
                .append("addl ")
                .append(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)))
                .append(", ")
                .append(registers.get(node));
    }

    private static void binary(
        StringBuilder builder,
        Map<Node, Register> registers,
        BinaryOperationNode node,
        String opcode
    ) {
        builder.repeat(" ", 2).append(registers.get(node))
            .append(" = ")
            .append(opcode)
            .append(" ")
            .append(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)))
            .append(" ")
            .append(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)));
    }
}
