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
import edu.kit.kastel.vads.compiler.myir.node.AssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.BinaryAssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.BooleanConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.CallAssignmentNode;
import edu.kit.kastel.vads.compiler.myir.node.ConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.IfNode;
import edu.kit.kastel.vads.compiler.myir.node.IntegerConstantNode;
import edu.kit.kastel.vads.compiler.myir.node.JumpNode;
import edu.kit.kastel.vads.compiler.myir.node.LabelNode;
import edu.kit.kastel.vads.compiler.myir.node.Node;
import edu.kit.kastel.vads.compiler.myir.node.ProgramNode;
import edu.kit.kastel.vads.compiler.myir.node.PureExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.ReturnNode;
import edu.kit.kastel.vads.compiler.myir.node.VariableNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.AddExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.BinaryExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.BitwiseAndExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.BitwiseOrExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.BitwiseXorExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.DivisionExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.EqualExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.GreaterThanExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.LessThanExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.ModuloExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.MultiplyExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.NotEqualExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.ShiftLeftExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.ShiftRightExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.binop.SubtractExpressionNode;
import edu.kit.kastel.vads.compiler.myir.node.block.BasicBlock;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SSAInstructionSelection implements Visitor<List<X86Statement>> {

    private final Map<VariableNode, Operand> registers = new HashMap<>();
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
            case ConstantNode<?> expressionNode -> List.of(new MoveInstruction(getOperand(expressionNode), targetOperand, BitSize.BIT32));
            case VariableNode expressionNode -> List.of(new MoveInstruction(getOperand(expressionNode), targetOperand, BitSize.BIT32));
            case BinaryExpressionNode binaryExpressionNode -> getBinaryAssignmentInstructions(binaryExpressionNode, targetOperand);
        };
    }

    @Override
    public List<X86Statement> visitBinaryAssignment(BinaryAssignmentNode node) {
        Operand targetOperand = getOperand(node.variableNode());

        return getBinaryAssignmentInstructions(node.binaryExpressionNode(), targetOperand);
    }

    @Override
    public List<X86Statement> visitCallAssignment(CallAssignmentNode node) {
        // TODO: Implement
        return List.of();
    }

    @Override
    public List<X86Statement> visitBasicBlock(BasicBlock node) {
        assert false;
        return List.of();
    }

    @Override
    public List<X86Statement> visitAddExpression(AddExpressionNode node) {
        assert false;
        return List.of();
    }

    @Override
    public List<X86Statement> visitSubtractExpression(SubtractExpressionNode node) {
        assert false;
        return List.of();
    }

    @Override
    public List<X86Statement> visitShiftRight(ShiftRightExpressionNode node) {
        assert false;
        return List.of();
    }

    @Override
    public List<X86Statement> visitShiftLeft(ShiftLeftExpressionNode node) {
        assert false;
        return List.of();
    }

    @Override
    public List<X86Statement> visitBitwiseOrExpression(BitwiseOrExpressionNode node) {
        assert false;
        return List.of();
    }

    @Override
    public List<X86Statement> visitBitwiseAndExpression(BitwiseAndExpressionNode node) {
        assert false;
        return List.of();
    }

    @Override
    public List<X86Statement> visitBitwiseXorExpression(BitwiseXorExpressionNode node) {
        assert false;
        return List.of();
    }

    @Override
    public List<X86Statement> visitMultiplyExpression(MultiplyExpressionNode node) {
        assert false;
        return List.of();
    }

    @Override
    public List<X86Statement> visitDivisionExpression(DivisionExpressionNode node) {
        assert false;
        return List.of();
    }

    @Override
    public List<X86Statement> visitModuloExpression(ModuloExpressionNode node) {
        assert false;
        return List.of();
    }

    @Override
    public List<X86Statement> visitGreaterThanExpression(GreaterThanExpressionNode node) {
        assert false;
        return List.of();
    }

    @Override
    public List<X86Statement> visitLessThanExpression(LessThanExpressionNode node) {
        assert false;
        return List.of();
    }

    @Override
    public List<X86Statement> visitEqualExpression(EqualExpressionNode node) {
        assert false;
        return List.of();
    }

    @Override
    public List<X86Statement> visitNotEqualExpression(NotEqualExpressionNode node) {
        assert false;
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

    private List<X86Statement> getBinaryAssignmentInstructions(BinaryExpressionNode binaryExpressionNode, Operand targetOperand) {
        Operand leftOperand = getOperand(binaryExpressionNode.left());
        Operand rightOperand = getOperand(binaryExpressionNode.right());

        return switch (binaryExpressionNode) {
            case AddExpressionNode addExpressionNode -> getInstructions(leftOperand, rightOperand, targetOperand, BinaryOperationInstruction.Operation.ADD);
            case SubtractExpressionNode subtractExpressionNode -> getInstructions(leftOperand, rightOperand, targetOperand, BinaryOperationInstruction.Operation.SUB);
            case ShiftRightExpressionNode shiftRightExpressionNode -> getInstructions(leftOperand, rightOperand, targetOperand, BinaryOperationInstruction.Operation.SHIFT_RIGHT);
            case ShiftLeftExpressionNode shiftLeftExpressionNode -> getInstructions(leftOperand, rightOperand, targetOperand, BinaryOperationInstruction.Operation.SHIFT_LEFT);
            case BitwiseOrExpressionNode bitwiseOrExpressionNode -> getInstructions(leftOperand, rightOperand, targetOperand, BinaryOperationInstruction.Operation.OR);
            case BitwiseXorExpressionNode bitwiseXorExpressionNode -> getInstructions(leftOperand, rightOperand, targetOperand, BinaryOperationInstruction.Operation.XOR);
            case BitwiseAndExpressionNode bitwiseAndExpressionNode -> getInstructions(leftOperand, rightOperand, targetOperand, BinaryOperationInstruction.Operation.AND);
            case MultiplyExpressionNode multiplyExpressionNode -> getMultiplyInstructions(leftOperand, rightOperand, targetOperand);
            case GreaterThanExpressionNode greaterThanExpressionNode -> getCompareInstructions(leftOperand, rightOperand, targetOperand, Condition.GREATER);
            case LessThanExpressionNode lessThanExpressionNode -> getCompareInstructions(leftOperand, rightOperand, targetOperand, Condition.LESS);
            case EqualExpressionNode equalExpressionNode -> getCompareInstructions(leftOperand, rightOperand, targetOperand, Condition.EQUALS);
            case NotEqualExpressionNode notEqualExpressionNode -> getCompareInstructions(leftOperand, rightOperand, targetOperand, Condition.NOT_EQUALS);
            case DivisionExpressionNode divisionExpressionNode -> getDivisionInstructions(leftOperand, rightOperand, targetOperand);
            case ModuloExpressionNode moduloExpressionNode -> getModuloInstructions(leftOperand, rightOperand, targetOperand);
        };
    }

    private List<X86Statement> getInstructions(Operand leftOperand, Operand rightOperand, Operand targetOperand, BinaryOperationInstruction.Operation operation) {
        return List.of(
                new MoveInstruction(leftOperand, targetOperand, BitSize.BIT32),
                new BinaryOperationInstruction(rightOperand, targetOperand, operation, BitSize.BIT32)
        );
    }

    private List<X86Statement> getModuloInstructions(Operand leftOperand, Operand rightOperand, Operand targetOperand) {
        return List.of(
                new MoveInstruction(Register.DATA, targetOperand, BitSize.BIT32),
                new MoveInstruction(leftOperand, Register.ACCUMULATOR, BitSize.BIT32),
                new SignExtendInstruction(),
                new SignedDivisionInstruction(rightOperand, BitSize.BIT32),
                new MoveInstruction(Register.DATA, Register.ACCUMULATOR, BitSize.BIT32),
                new MoveInstruction(targetOperand, Register.DATA, BitSize.BIT32),
                new MoveInstruction(Register.ACCUMULATOR, targetOperand, BitSize.BIT32)
        );
    }

    private List<X86Statement> getDivisionInstructions(Operand leftOperand, Operand rightOperand, Operand targetOperand) {
        return List.of(
                new MoveInstruction(Register.DATA, targetOperand, BitSize.BIT32),
                new MoveInstruction(leftOperand, Register.ACCUMULATOR, BitSize.BIT32),
                new SignExtendInstruction(),
                new SignedDivisionInstruction(rightOperand, BitSize.BIT32),
                new MoveInstruction(targetOperand, Register.DATA, BitSize.BIT32),
                new MoveInstruction(Register.ACCUMULATOR, targetOperand, BitSize.BIT32)
        );
    }

    private List<X86Statement> getMultiplyInstructions(Operand leftOperand, Operand rightOperand, Operand targetOperand) {
        return List.of(
                new MoveInstruction(Register.DATA, targetOperand, BitSize.BIT32),
                new MoveInstruction(leftOperand, Register.ACCUMULATOR, BitSize.BIT32),
                new SignedMultiplyInstruction(rightOperand, BitSize.BIT32),
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
                    registers.computeIfAbsent(variableNode, _ -> new VirtualOperand(registerCounter++));
            default -> throw new IllegalStateException("Unexpected value: " + node);
        };
    }
}
