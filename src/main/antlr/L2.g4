// -------------------- Lexer --------------------
grammar L2;
// TODO:Einheitlich machen

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
FLUSH: 'flush';

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
BITNOT: '~';
SHIFT_LEFT: '<<';
SHIFT_RIGHT: '>>';

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

program: (function)* EOF;

function: type identifier parameters block;

parameters: LPAREN ((parameter COMMA)* parameter)? RPAREN;

parameter: type identifier;

block: LBRACE (statement)* RBRACE;

statement
    : simple SEMI
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

simple
    : assignment
    | declaration
    | call
    ;

assignment: <assoc=right>  leftValue assignOperator expression;

leftValue: identifier | LPAREN leftValue RPAREN;

declaration
    : type identifier
    | type identifier ASSIGN expression
    ;

if: IF LPAREN expression RPAREN ifStatement=statement (ELSE elseStatement=statement)?;

while: WHILE LPAREN expression RPAREN statement;

for: FOR LPAREN (forDeclaration=simple)? SEMI forExpression=expression SEMI (forAssignment=simple)? RPAREN statement;

return: RETURN expression SEMI;

expression
    : booleanConstant # BooleanExpression
    | identifier # IdentiferExpression
    | LPAREN expression RPAREN # ParenExpression
    | intConstant # IntegerExpression
    | <assoc=right> unaryOperator expression # UnaryExpression
    | left=expression binaryOperatorDot right=expression # BinaryExpression
    | left=expression binaryOperatorLine riht=expression # BinaryExpression
    | left=expression arithmeticShift right=expression # BinaryExpression
    | left=expression integerComparison right=expression # BinaryExpression
    | left=expression equality right=expression # BinaryExpression
    | left=expression BITAND right=expression # BinaryExpression
    | left=expression BITXOR right=expression # BinaryExpression
    | left=expression BITOR right=expression # BinaryExpression
    | left=expression LOGAND right=expression # BinaryExpression
    | left=expression LOGOR right=expression # BinaryExpression
    | <assoc=right> left=expression QUESTION right=expression COLON expression # TernaryExpression
    | call #CallExpression
    ;

call
    : PRINT arguments
    | READ arguments
    | FLUSH arguments
    | identifier arguments
    ;

arguments: LPAREN (expression (COMMA expression)*)? RPAREN;

identifier: IDENT;

type: INT | BOOL;

continue: CONTINUE SEMI;

break: BREAK SEMI;

unaryOperator: NOT | BITNOT | MINUS;

binaryOperatorDot: TIMES | DIV | MOD;

binaryOperatorLine: PLUS | MINUS;

arithmeticShift: SHIFT_LEFT | SHIFT_RIGHT;

integerComparison: LT | LE | GT | GE;

equality: EQ | NEQ;

assignOperator
    : ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | TIMES_ASSIGN | DIV_ASSIGN | MOD_ASSIGN
    | AND_ASSIGN | XOR_ASSIGN | OR_ASSIGN | SHIFT_LEFT_ASSIGN | SHIFT_RIGHT_ASSIGN
    ;

booleanConstant: TRUE | FALSE;

intConstant: DECNUM | HEXNUM;