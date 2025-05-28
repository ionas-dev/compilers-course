package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.antlr.L2BaseVisitor;
import edu.kit.kastel.vads.compiler.antlr.L2Parser;
import edu.kit.kastel.vads.compiler.parser.ast.ProgramTree;
import edu.kit.kastel.vads.compiler.parser.visitor.RecursivePostorderVisitor;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class SemanticAnalysis {

    private final L2Parser.ProgramContext program;

    public SemanticAnalysis(L2Parser.ProgramContext program) {
        this.program = program;
    }

    public void analyze() {
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new IntegerLiteralRangeAnalysis(), program);
        walker.walk(new VariableStatusAnalysis(), program);
        walker.walk(new ReturnAnalysis(), program);
    }

}
