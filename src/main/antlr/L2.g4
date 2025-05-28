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

program: INT 'main' LPAREN RPAREN block EOF;
program: INT main LPAREN RPAREN block EOF;

main: IDENT {$IDENT.text.equals("main")}?;

block: LBRACE statements RBRACE;

statements: (statement)*;

statement: simple SEMI
    | control
    | block
    ;

assignment: leftValue assignOperator expression;

simple: assignment
    | declaration
    ;

simpleOptional: /* empty */
    | simple
    ;

identifier: IDENT;

leftValue: identifier | LPAREN leftValue RPAREN;

declaration: type identifier
    | type identifier ASSIGN expression;

type: INT | BOOL;

control
    : IF LPAREN expression RPAREN statement elseOptional
    | WHILE LPAREN expression RPAREN statement
    | FOR LPAREN simpleOptional SEMI expression SEMI simpleOptional RPAREN statement
    | CONTINUE SEMI
    | BREAK SEMI
    | RETURN expression SEMI
    ;

elseOptional: /* empty */
       | ELSE statement
       ;

booleanConstant: TRUE | FALSE;

expression
    : booleanConstant
    | identifier
    | LPAREN expression RPAREN
    | intConstant
    | expression binaryOperator expression
    | unaryOperator expression
    | expression QUESTION expression COLON expression
    ;

intConstant: DECNUM | HEXNUM;

unaryOperator: NOT | BINARY_NOT | MINUS;

assignOperator
    : ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | TIMES_ASSIGN | DIV_ASSIGN | MOD_ASSIGN
    | AND_ASSIGN | XOR_ASSIGN | OR_ASSIGN | SHIFT_LEFT_ASSIGN | SHIFT_RIGHT_ASSIGN
    ;

binaryOperator
    : PLUS | MINUS | TIMES | DIV | MOD
    | LT | LE | GT | GE
    | EQ | NEQ
    | LOGAND | LOGOR
    | BITAND | BITXOR | BITOR
    | SHIFT_LEFT | SHRIFT_RIGHT
    ;
