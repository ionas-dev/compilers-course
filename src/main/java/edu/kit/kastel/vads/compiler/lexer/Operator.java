package edu.kit.kastel.vads.compiler.lexer;

import edu.kit.kastel.vads.compiler.Span;

public record Operator(OperatorType type, Span span) implements Token {

    @Override
    public boolean isOperator(OperatorType operatorType) {
        return type() == operatorType;
    }

    @Override
    public String asString() {
        return type().toString();
    }

    public enum OperatorType {
        ASSIGN_MINUS("-="),
        MINUS("-"),

        ASSIGN_PLUS("+="),
        PLUS("+"),

        ASSIGN_MUL("*="),
        MUL("*"),

        ASSIGN_DIV("/="),
        DIV("/"),

        ASSIGN_MOD("%="),
        MOD("%"),

        ASSIGN_BITWISE_AND("&="),
        BITWISE_AND("&"),

        ASSIGN_BITWISE_OR("|="),
        BITWISE_OR("|"),

        ASSIGN_BITWISE_XOR("^="),
        BITWISE_XOR("^"),

        BITWISE_NOT("~"),

        ASSIGN_SHIFT_LEFT("<<="),
        SHIFT_LEFT("<<"),

        ASSIGN_SHIFT_RIGHT(">>="),
        SHIFT_RIGHT(">>"),

        EQUALS("=="),
        ASSIGN("="),
        NOT_EQUALS("!="),
        LESS_THAN_EQUALS("<="),
        LESS_THAN("<"),
        GREATER_THAN_EQUALS(">="),
        GREATER_THAN(">"),

        LOGICAL_NOT("!"),
        LOGICAL_AND("&&"),
        LOGICAL_OR("||");

        private final String value;

        OperatorType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
