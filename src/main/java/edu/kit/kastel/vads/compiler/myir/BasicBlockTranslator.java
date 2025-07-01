package edu.kit.kastel.vads.compiler.myir;

import edu.kit.kastel.vads.compiler.myir.node.CommandNode;
import edu.kit.kastel.vads.compiler.myir.node.EndNode;
import edu.kit.kastel.vads.compiler.myir.node.ProgramNode;
import edu.kit.kastel.vads.compiler.myir.node.StartNode;
import edu.kit.kastel.vads.compiler.myir.node.block.BasicBlock;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BasicBlockTranslator implements Visitor<Collection<BasicBlock>> {

    public static Collection<BasicBlock> translate(ProgramNode program) {
        return program.accept(new BasicBlockTranslator());
    }

    @Override
    public Collection<BasicBlock> visitProgram(ProgramNode node) {
        Collection<BasicBlock> basicBlocks = new ArrayList<>();
        List<List<CommandNode>> startedBlockCommands = new ArrayList<>();
        boolean started = false;

        for (CommandNode commandNode: node.children()) {
            if (commandNode instanceof StartNode) {
                started = true;
                startedBlockCommands.add(new ArrayList<>());
                startedBlockCommands.getLast().add(commandNode);
            } else if (commandNode instanceof EndNode) {
                if (!started) {
                    continue;
                }

                started = false;
                List<CommandNode> commands = startedBlockCommands.removeLast();
                commands.add(commandNode);
                basicBlocks.add(new BasicBlock(commands));
            } else {
                startedBlockCommands.getLast().add(commandNode);
            }
        }
        return basicBlocks;
    }
}
