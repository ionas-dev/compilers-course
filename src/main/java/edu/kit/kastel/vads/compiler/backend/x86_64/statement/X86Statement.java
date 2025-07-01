package edu.kit.kastel.vads.compiler.backend.x86_64.statement;

import edu.kit.kastel.vads.compiler.backend.common.statement.Statement;

// TODO: Think about generic usage to differentiate statements before and after register allocation
public sealed interface X86Statement extends Statement permits BinaryOperationInstruction, CallInstruction, Comment, CompareInstruction, EmptyStatement, GlobalDirective, JumpIfInstruction, JumpInstruction, Label, MoveInstruction, ReturnInstruction, SetInstruction, SignExtendInstruction, SignedDivisionInstruction, SignedMultiplyInstruction, SyscallInstruction, TestInstruction, TextDirective { }
