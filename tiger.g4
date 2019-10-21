grammar tiger;


MAIN : 'main';
COMMA : ',';
COLON : ':';
SEMI : ';';
LPAREN : '(';
RPAREN : ')';
LBRACK : '[';
RBRACK : ']';
LBRACE : '{';
RBRACE : '}';
PERIOD : '.';
EQUALS : '=';
PLUS : '+';
MINUS : '-';
MULT : '*';
DIV : '/';
EXP: '**';
EQ : '==';
NEQ : '!=';
LESSER : '<';
GREATER : '>';
LESSEREQ : '<=';
GREATEREQ : '>=';
AND : '&';
OR : '|';
ASSIGN : ':=';
ARRAY : 'array';
RECORD : 'record'; 
BREAK : 'break';
DO : 'do';
ELSE : 'else';
END : 'end';
FOR : 'for';
FUNC : 'function';
IF : 'if';
IN : 'in';
LET : 'let';
OF : 'of';
THEN : 'then';
TO : 'to';
TYPE : 'type';
VAR : 'var';
WHILE : 'while';
ENDIF : 'endif';
BEGIN : 'begin';
ENDDO : 'enddo';
RETURN : 'return';
INT : 'int';
FLOAT : 'float';
ID : [a-zA-Z_][a-zA-Z0-9_]*;
INTLIT : [+-]?[0-9]+;
FLOATLIT: [+-]?[0-9]+('.'[0-9]+)?([eE][+-]?[0-9]+)?;
COMMENT : '/*'[_a-zA-Z0-9\t\n\r ]*'*/' -> skip;
WS : [ \t\r\n]+ -> skip;

tiger_program : MAIN LET declaration_segment IN BEGIN stat_seq END;

declaration_segment : type_declaration_list var_declaration_list function_declaration_list;

type_declaration_list : type_declaration type_declaration_list | /* NULL */;

var_declaration_list : var_declaration var_declaration_list | /* NULL */;

function_declaration_list : function_declaration function_declaration_list | /* NULL */;

type_declaration : TYPE ID EQUALS type SEMI;
type : ARRAY LBRACK INTLIT RBRACK OF type_id | ID | type_id;
type_id : INT | FLOAT;

var_declaration : VAR id_list COLON type optional_init SEMI; 
id_list : ID id_list_tail;
id_list_tail : COMMA ID id_list_tail | /* NULL */;
optional_init : ASSIGN constant | /* NULL */;

function_declaration : FUNC ID LPAREN param_list RPAREN ret_type BEGIN stat_seq END SEMI; 
param_list : param param_list_tail | /* NULL */;
param_list_tail : COMMA param param_list_tail | /* NULL */;
 
ret_type : COLON type;
param : ID COLON type;

stat_seq : stat stat_seq_tail;
stat_seq_tail : stat stat_seq_tail | /* NULL */;

stat :  IF expr THEN stat_seq else_stat ENDIF SEMI |
        WHILE expr DO stat_seq ENDDO SEMI |
        FOR ID ASSIGN expr TO expr DO stat_seq ENDDO SEMI |
        BREAK SEMI |
        RETURN expr SEMI |
        LET declaration_segment IN stat_seq END SEMI |
        ID id_tail;

id_tail :   ASSIGN assign_tail |
            LBRACK expr RBRACK ASSIGN expr SEMI |
            LPAREN expr_list RPAREN SEMI;

assign_tail: expr SEMI | ID LPAREN expr_list RPAREN SEMI;

lvalue : ID lvalue_tail;
lvalue_tail : LBRACK expr RBRACK | /* NULL */;

else_stat : ELSE stat_seq | /* NULL */;

// <expr> is our “<or-term>”
expr : and_term e_tail;
e_tail : OR and_term e_tail | /* NULL */;

and_term : comparison_term and_tail;
and_tail : AND comparison_term and_tail | /* NULL */;

comparison_term: div_term comparison_tail;
comparison_tail : (EQ | NEQ | LESSER | LESSEREQ | GREATER | GREATEREQ) div_term | /* NULL */;

div_term : mult_term div_tail;
div_tail : DIV mult_term div_tail | /* NULL */;

mult_term : sub_term mult_tail;
mult_tail : MULT sub_term mult_tail | /* NULL */;

sub_term : add_term sub_tail;
sub_tail : MINUS add_term sub_tail | /* NULL */; 

add_term : pow_term add_tail;
add_tail : PLUS pow_term add_tail | /* NULL */;

pow_term : factor pow_tail;
pow_tail : EXP factor pow_tail | /* NULL */;

factor : LPAREN expr RPAREN | constant |  lvalue;

constant : INTLIT | FLOATLIT;

expr_list : expr expr_list_tail | /* NULL */;
expr_list_tail : COMMA expr expr_list_tail | /* NULL */;
