package edu.kit.kastel.vads.compiler;

import edu.kit.kastel.vads.compiler.antlr.L2Lexer;
import edu.kit.kastel.vads.compiler.antlr.L2Parser;
import edu.kit.kastel.vads.compiler.backend.common.codegen.CodeGenerator;
import edu.kit.kastel.vads.compiler.backend.x86_64.codegen.X86Assembler;
import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.SsaTranslation;
import edu.kit.kastel.vads.compiler.ir.optimize.LocalValueNumbering;
import edu.kit.kastel.vads.compiler.ir.util.YCompPrinter;
import edu.kit.kastel.vads.compiler.semantic.SemanticAnalysis;
import edu.kit.kastel.vads.compiler.semantic.SemanticException;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 2) {
            System.err.println("Invalid arguments: Expected one input file and one output file");
            System.exit(3);
        }
        Path input = Path.of(args[0]);
        Path output = Path.of(args[1]);
        L2Parser.ProgramContext program = lexAndParse(input);
        try {
            // TODO: Should probably recognize semantic error for test:use-uninitialized-variable
            new SemanticAnalysis(program).analyze();
        } catch (SemanticException e) {
            e.printStackTrace();
            System.exit(7);
            return;
        }
        List<IrGraph> graphs = new ArrayList<>();
        SsaTranslation translation = new SsaTranslation(program, new LocalValueNumbering());
        graphs.add(translation.translate());

        if ("vcg".equals(System.getenv("DUMP_GRAPHS")) || "vcg".equals(System.getProperty("dumpGraphs"))) {
            Path tmp = output.toAbsolutePath().resolveSibling("graphs");
            Files.createDirectory(tmp);
            for (IrGraph graph : graphs) {
                dumpGraph(graph, tmp, "before-codegen");
            }
        }

        CodeGenerator generator = new X86Assembler();
        try {
            String s = generator.generateCode(graphs);
            Path asmOutput = Path.of(output + ".s");
            Files.writeString(asmOutput, s);
            println("Generated asm file: ", asmOutput.toAbsolutePath().toString());

            int exitValue = compileMachineCode(asmOutput, output);
            if (exitValue != 0) {
                // TODO: Throw more meaningful exception
                throw new RuntimeException("Compilation failed");
            }
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            System.exit(7);
            return;
        }
        println("Compiled to: ", output.toAbsolutePath().toString());
    }



    private static L2Parser.ProgramContext lexAndParse(Path input) throws IOException {
        try {
            L2Lexer lexer = new L2Lexer(CharStreams.fromPath(input));
            L2Parser parser = new L2Parser(new CommonTokenStream(lexer));
            return parser.program();
        } catch (RecognitionException e) {
            e.printStackTrace();
            System.exit(42);
            throw new AssertionError("unreachable");
        }
    }

    private static int compileMachineCode(Path inputPath, Path outputPath) throws InterruptedException, IOException {
        StringJoiner commandBuilder = new StringJoiner(" ");

        if (isARM()) {
            commandBuilder.add("arch -x86_64");
        }

        commandBuilder
                .add("gcc")
                .add(inputPath.toAbsolutePath().toString())
                .add("-o")
                .add(outputPath.toAbsolutePath().toString());

        String command = commandBuilder.toString();
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.inheritIO();

        println("Execute process: ", command);
        Process process = processBuilder.start();
        process.waitFor();
        return process.exitValue();
    }

    private static boolean isARM() {
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();

        if (os.contains("mac") && arch.equals("aarch64")) {
            println("MacOSX with ARM architecture detected");
            return true;
        } else {
            return false;
        }
    }

    public static void println(String... string) {
        String output = Arrays.stream(string).collect(Collectors.joining());
        System.out.println("L1C: " + output);
    }

    private static void dumpGraph(IrGraph graph, Path path, String key) throws IOException {
        Files.writeString(
            path.resolve(graph.name() + "-" + key + ".vcg"),
            YCompPrinter.print(graph)
        );
    }
}
