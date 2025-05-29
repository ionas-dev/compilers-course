package edu.kit.kastel.vads.compiler;

import org.antlr.v4.runtime.Token;

public sealed interface Position {
  int line();
  int column();

  record SimplePosition(int line, int column) implements Position {
    @Override
    public String toString() {
      return line() + ":" + column();
    }
  }


  static Position fromToken(Token token) {
    return new SimplePosition(token.getLine(), token.getCharPositionInLine());
  }

}
