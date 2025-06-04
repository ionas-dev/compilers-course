package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.antlr.L2Parser;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class SemanticAnalysis {

    private final L2Parser.ProgramContext program;

    public SemanticAnalysis(L2Parser.ProgramContext program) {
        this.program = program;
    }

    public void analyze() {
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new IntegerLiteralRangeAnalysis(), program);
        program.accept(new VariableStatusAnalysis());
        program.accept(new LoopAnalysis());
        program.accept(new ReturnAnalysis());
        program.accept(new TypeAnalysis());
    }

}
