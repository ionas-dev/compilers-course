package edu.kit.kastel.vads.compiler;

import org.antlr.v4.runtime.Token;

public sealed interface Span {
    Position start();
    Position end();

    Span merge(Span later);

    record SimpleSpan(Position start, Position end) implements Span {
        @Override
        public Span merge(Span later) {
            return new SimpleSpan(start(), later.end());
        }

        @Override
        public String toString() {
            return "[" + start() + "|" + end() + "]";
        }
    }

    static Span fromStartAndEndToken(Token startToken, Token endToken) {
        Position startPosition = Position.fromToken(startToken);
        Position endPosition = Position.fromToken(endToken);
        return new SimpleSpan(startPosition, endPosition);
    }
}
