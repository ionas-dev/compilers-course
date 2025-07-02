package edu.kit.kastel.vads.compiler.backend.x86_64.instructionselection;

import edu.kit.kastel.vads.compiler.backend.common.operand.Operand;
import edu.kit.kastel.vads.compiler.backend.common.operand.VirtualOperand;
import edu.kit.kastel.vads.compiler.backend.common.util.BitSize;
import edu.kit.kastel.vads.compiler.backend.x86_64.operand.ImmediateOperand;
import edu.kit.kastel.vads.compiler.backend.x86_64.operand.Register;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.BinaryOperationInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.CompareInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.JumpIfInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.JumpInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.Label;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.MoveInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.ReturnInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.SetInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.SignExtendInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.SignedDivisionInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.SignedMultiplyInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.TestInstruction;
import edu.kit.kastel.vads.compiler.backend.x86_64.statement.X86Statement;
import edu.kit.kastel.vads.compiler.backend.x86_64.util.Condition;
import edu.kit.kastel.vads.compiler.myir.node.ConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.Node;
import edu.kit.kastel.vads.compiler.myir.node.ProgramNode;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.AssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.BinaryAssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.BinaryExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.BooleanConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.CallAssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.IfNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.IntegerConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.JumpNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.LabelNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.ReturnNode;
import edu.kit.kastel.vads.compiler.myir.node.sealed.VariableNode;
import edu.kit.kastel.vads.compiler.myir.node.visitor.CommandVisitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SSAInstructionSelection extends CommandVisitor<List<X86Statement>> {

    private final Map<String, Operand> registers = new HashMap<>();
    private int registerCounter = 0;

    public SSAInstructionSelection() {
    }

    public static List<X86Statement> translate(ProgramNode program) {
        return program.accept(new SSAInstructionSelection());
    }

    @Override
    public List<X86Statement> visitProgram(ProgramNode node) {
        return node.children().stream()
                .map(this::visitNode)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<X86Statement> visitNode(Node node) {
        return node.accept(this);
    }

    @Override
    public List<X86Statement> visitLabel(LabelNode node) {
        return List.of(new Label(node.value()));
    }

    @Override
    public List<X86Statement> visitReturn(ReturnNode node) {
        return List.of(
                new MoveInstruction(getOperand(node.expression()), Register.ACCUMULATOR, BitSize.BIT32),
                new ReturnInstruction()
        );
    }

    @Override
    public List<X86Statement> visitJump(JumpNode node) {
        return List.of(new JumpInstruction(node.label()));
    }

    @Override
    public List<X86Statement> visitIf(IfNode node) {
        Operand operand = getOperand(node.expression());
        return List.of(
                new TestInstruction(operand, new VirtualOperand(registerCounter++), BitSize.BIT32),
                new JumpIfInstruction(node.ifJump().label(), Condition.NOT_EQUALS)
        );
    }

    @Override
    public List<X86Statement> visitAssignment(AssignmentNode node) {
        Operand targetOperand = getOperand(node.variable());

        return switch (node.expression()) {
            case ConstantNode<?> expressionNode ->
                    List.of(new MoveInstruction(getOperand(expressionNode), targetOperand, BitSize.BIT32));
            case VariableNode expressionNode ->
                    List.of(new MoveInstruction(getOperand(expressionNode), targetOperand, BitSize.BIT32));
            case BinaryExpressionNode binaryExpressionNode -> {
                Operand leftOperand = getOperand(binaryExpressionNode.left());
                Operand rightOperand = getOperand(binaryExpressionNode.right());

                yield switch (binaryExpressionNode.type()) {
                    case ADDITION ->
                            getInstructions(leftOperand, rightOperand, targetOperand, BinaryOperationInstruction.Operation.ADD);
                    case SUBTRACTION ->
                            getInstructions(leftOperand, rightOperand, targetOperand, BinaryOperationInstruction.Operation.SUB);
                    case SHIFT_RIGHT ->
                            getInstructions(leftOperand, rightOperand, targetOperand, BinaryOperationInstruction.Operation.SHIFT_RIGHT);
                    case SHIFT_LEFT ->
                            getInstructions(leftOperand, rightOperand, targetOperand, BinaryOperationInstruction.Operation.SHIFT_LEFT);
                    case BITWISE_OR ->
                            getInstructions(leftOperand, rightOperand, targetOperand, BinaryOperationInstruction.Operation.OR);
                    case BITWISE_XOR ->
                            getInstructions(leftOperand, rightOperand, targetOperand, BinaryOperationInstruction.Operation.XOR);
                    case BITWISE_AND ->
                            getInstructions(leftOperand, rightOperand, targetOperand, BinaryOperationInstruction.Operation.AND);
                    case MULTIPLICATION -> getMultiplyInstructions(leftOperand, rightOperand, targetOperand);
                    case GREATER_THAN ->
                            getCompareInstructions(leftOperand, rightOperand, targetOperand, Condition.GREATER);
                    case LESS_THAN -> getCompareInstructions(leftOperand, rightOperand, targetOperand, Condition.LESS);
                    case GREATER_EQUAL ->
                            getCompareInstructions(leftOperand, rightOperand, targetOperand, Condition.GREATER_EQUALS);
                    case LESS_EQUAL ->
                            getCompareInstructions(leftOperand, rightOperand, targetOperand, Condition.LESS_EQUALS);
                    case EQUAL -> getCompareInstructions(leftOperand, rightOperand, targetOperand, Condition.EQUALS);
                    case NOT_EQUAL ->
                            getCompareInstructions(leftOperand, rightOperand, targetOperand, Condition.NOT_EQUALS);
                };
            }
        };
    }

    @Override
    public List<X86Statement> visitBinaryAssignment(BinaryAssignmentNode node) {
        Operand targetOperand = getOperand(node.variableNode());
        Operand leftOperand = getOperand(node.leftExpression());
        Operand rightOperand = getOperand(node.rightExpression());

        return switch (node.binaryExpressionType()) {
            case DIVISION ->
                    getDivisionInstructions(leftOperand, rightOperand, targetOperand);
            case MODULO ->
                    getModuloInstructions(leftOperand, rightOperand, targetOperand);
        };
    }

    @Override
    public List<X86Statement> visitCallAssignment(CallAssignmentNode node) {
        // TODO: Implement
        return List.of();
    }

    @Override
    public List<X86Statement> visitBooleanConstant(BooleanConstantNode node) {
        assert false;
        return List.of();
    }

    @Override
    public List<X86Statement> visitIntegerConstant(IntegerConstantNode node) {
        assert false;
        return List.of();
    }

    @Override
    public List<X86Statement> visitVariable(VariableNode node) {
        assert false;
        return List.of();
    }

    private List<X86Statement> getInstructions(Operand leftOperand, Operand rightOperand, Operand targetOperand, BinaryOperationInstruction.Operation operation) {
        return List.of(
                new MoveInstruction(leftOperand, targetOperand, BitSize.BIT32),
                new BinaryOperationInstruction(rightOperand, targetOperand, operation, BitSize.BIT32)
        );
    }

    private List<X86Statement> getModuloInstructions(Operand leftOperand, Operand rightOperand, Operand targetOperand) {
        Operand temporaryOperand = new VirtualOperand(registerCounter++);

        return List.of(
                new MoveInstruction(Register.DATA, targetOperand, BitSize.BIT32),
                new MoveInstruction(leftOperand, Register.ACCUMULATOR, BitSize.BIT32),
                new SignExtendInstruction(),
                new MoveInstruction(rightOperand, temporaryOperand, BitSize.BIT32),
                new SignedDivisionInstruction(temporaryOperand, BitSize.BIT32),
                new MoveInstruction(Register.DATA, Register.ACCUMULATOR, BitSize.BIT32),
                new MoveInstruction(targetOperand, Register.DATA, BitSize.BIT32),
                new MoveInstruction(Register.ACCUMULATOR, targetOperand, BitSize.BIT32)
        );
    }

    private List<X86Statement> getDivisionInstructions(Operand leftOperand, Operand rightOperand, Operand targetOperand) {
        Operand temporaryOperand = new VirtualOperand(registerCounter++);

        return List.of(
                new MoveInstruction(Register.DATA, targetOperand, BitSize.BIT32),
                new MoveInstruction(leftOperand, Register.ACCUMULATOR, BitSize.BIT32),
                new SignExtendInstruction(),
                new MoveInstruction(rightOperand, temporaryOperand, BitSize.BIT32),
                new SignedDivisionInstruction(temporaryOperand, BitSize.BIT32),
                new MoveInstruction(targetOperand, Register.DATA, BitSize.BIT32),
                new MoveInstruction(Register.ACCUMULATOR, targetOperand, BitSize.BIT32)
        );
    }

    private List<X86Statement> getMultiplyInstructions(Operand leftOperand, Operand rightOperand, Operand targetOperand) {
        return List.of(
                new MoveInstruction(Register.DATA, targetOperand, BitSize.BIT32),
                new MoveInstruction(leftOperand, Register.ACCUMULATOR, BitSize.BIT32),
                new MoveInstruction(rightOperand, Register.DATA, BitSize.BIT32),
                new SignedMultiplyInstruction(Register.DATA, BitSize.BIT32),
                new MoveInstruction(targetOperand, Register.DATA, BitSize.BIT32),
                new MoveInstruction(Register.ACCUMULATOR, targetOperand, BitSize.BIT32)
        );
    }

    private List<X86Statement> getCompareInstructions(Operand leftOperand, Operand rightOperand, Operand targetOperand, Condition condition) {
        return List.of(
                new CompareInstruction(rightOperand, leftOperand, BitSize.BIT32),
                new SetInstruction(targetOperand, condition, BitSize.BIT32)
        );
    }

    private Operand getOperand(PureExpressionNode node) {
        return switch (node) {
            case IntegerConstantNode integerConstantNode -> new ImmediateOperand(integerConstantNode.value());
            case BooleanConstantNode booleanConstantNode -> new ImmediateOperand(booleanConstantNode.value() ? 1L : 0L);
            case VariableNode variableNode ->
                    registers.computeIfAbsent(variableNode.name(), _ -> new VirtualOperand(registerCounter++));
            default -> throw new IllegalStateException("Unexpected value: " + node);
        };
    }
}
