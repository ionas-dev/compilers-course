// -------------------- Lexer --------------------
grammar L2;

// Whitespace and comments
WHITESPACE: [ \t\r\n]+ -> skip;
LINE_COMMENT: '//' ~[\r\n]* -> skip;
MULTILINE_COMMENT: '/*' ( MULTILINE_COMMENT | ~[*/] | '*' ~'/' | '/' ~'*' )* '*/' -> skip;

// Keywords
STRUCT: 'struct';
IF: 'if';
ELSE: 'else';
WHILE: 'while';
FOR: 'for';
CONTINUE: 'continue';
BREAK: 'break';
RETURN: 'return';
ASSERT: 'assert';
TRUE: 'true';
FALSE: 'false';
NULL: 'NULL';
PRINT: 'print';
READ: 'read';
ALLOC: 'alloc';
ALLOC_ARRAY: 'alloc_array';
INT: 'int';
BOOL: 'bool';
VOID: 'void';
CHAR: 'char';
STRING: 'string';

// Operators
ASSIGN: '=';
PLUS_ASSIGN: '+=';
MINUS_ASSIGN: '-=';
TIMES_ASSIGN: '*=';
DIV_ASSIGN: '/=';
MOD_ASSIGN: '%=';
AND_ASSIGN: '&=';
XOR_ASSIGN: '^=';
OR_ASSIGN: '|=';
SHIFT_LEFT_ASSIGN: '<<=';
SHIFT_RIGHT_ASSIGN: '>>=';

PLUS: '+';
MINUS: '-';
TIMES: '*';
DIV: '/';
MOD: '%';

LT: '<';
LE: '<=';
GT: '>';
GE: '>=';
EQ: '==';
NEQ: '!=';

BITAND: '&';
BITOR: '|';
BITXOR: '^';
LOGAND: '&&';
LOGOR: '||';

NOT: '!';
BINARY_NOT: '~';
SHIFT_LEFT: '<<';
SHRIFT_RIGHT: '>>';

QUESTION: '?';
COLON: ':';

// Delimiters
LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';
SEMI: ';';
COMMA: ',';

// Literals
DECNUM: '0' | [1-9] [0-9]*;
HEXNUM: '0' [xX] [a-fA-F0-9]+;

// Identifiers
IDENT: [a-zA-Z_] [a-zA-Z0-9_]*;

// -------------------- Parser --------------------

program: INT main LPAREN RPAREN block EOF;

main: IDENT {$IDENT.text.equals("main")}?;

block: LBRACE (statement)* RBRACE;

statement: simple SEMI
    | control
    | block
    ;

control
    : if
    | while
    | for
    | continue
    | break
    | return
    ;

simple: assignment
    | declaration
    ;

simpleOptional
    : /* empty */
    | simple
    ;

assignment: <assoc=right>  leftValue assignOperator expression;

leftValue: identifier | LPAREN leftValue RPAREN;

declaration: type identifier
    | type identifier ASSIGN expression;

if: IF LPAREN expression RPAREN statement elseOptional;

elseOptional
    : /* empty */
    | ELSE statement
    ;

while: WHILE LPAREN expression RPAREN statement;

for: FOR LPAREN simpleOptional SEMI expression SEMI simpleOptional RPAREN statement;

return: RETURN expression SEMI;

expression
    : booleanConstant # BooleanExpression
    | identifier # IdentiferExpression
    | LPAREN expression RPAREN # ParenExpression
    | intConstant # IntegerExpression
    | <assoc=right> unaryOperator expression # UnaryExpression
    | expression binaryOperatorDot expression # BinaryExpression
    | expression binaryOperatorLine expression # BinaryExpression
    | expression arithmeticShift expression # BinaryExpression
    | expression integerComparison expression # BinaryExpression
    | expression equality expression # BinaryExpression
    | expression BITAND expression # BinaryExpression
    | expression BITXOR expression # BinaryExpression
    | expression BITOR expression # BinaryExpression
    | expression LOGAND expression # BinaryExpression
    | expression LOGOR expression # BinaryExpression
    | <assoc=right> expression QUESTION expression COLON expression # TernaryExpression
    ;

identifier: IDENT;

type: INT | BOOL;

continue: CONTINUE SEMI;

break: BREAK SEMI;

unaryOperator: NOT | BINARY_NOT | MINUS;

binaryOperatorDot: TIMES | DIV | MOD;

binaryOperatorLine: PLUS | MINUS;

arithmeticShift: SHIFT_LEFT | SHRIFT_RIGHT;

integerComparison: LT | LE | GT | GE;

equality: EQ | NEQ;

assignOperator
    : ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | TIMES_ASSIGN | DIV_ASSIGN | MOD_ASSIGN
    | AND_ASSIGN | XOR_ASSIGN | OR_ASSIGN | SHIFT_LEFT_ASSIGN | SHIFT_RIGHT_ASSIGN
    ;

booleanConstant: TRUE | FALSE;

intConstant: DECNUM | HEXNUM;