grammar Tiger;


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
FUNC : 'func';
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
END : 'end';
ENDDO : 'enddo';
ID : [a-zA-Z_][a-zA-Z0-9_]*;
INTLIT : [+-]?[0-9]+;
FLOATLIT: [+-]?[0-9]+(.[0-9]+)?([eE][+-]?[0-9]+)?;
COMMENT : /\*.*?\*/;
NULL : '';
RETURN : 'RETURN';

tiger_program : MAIN LET declaration_segment IN BEGIN stat_seq END;

declaration_segment : type_declaration_list var_declaration_list function_declaration_list;

type_declaration_list : type_declaration type_declaration_list | NULL;

var_declaration_list : var_declaration var_declaration_list | NULL;

function_declaration_list : function_declaration function_declaration_list | NULL;

type_declaration : TYPE ID ASSIGN type SEMI;
type : ARRAY LBRACK INTLIT RBRACK OF type_id | ID | type_id;
type_id : INTLIT | FLOATLIT;

var_declaration : VAR id_list COLON type optional_init SEMI; 
id_list : ID id_list_tail;
id_list_tail : COMMA ID id_list_tail | NULL;
optional_init : ASSIGN const | NULL;

function_declaration : FUNC ID LPAREN param_list RPAREN ret_type BEGIN stat_seq END SEMI; 
param_list : param param_list_tail | NULL;
param_list_tail : COMMA param param_list_tail | NULL;
 
ret_type : COLON type | NULL;
param : ID COLON type;

stat_seq : stat stat_seq_tail;
stat_seq_tail : stat stat_seq_tail | NULL;


stat : lvalue l_tail ASSIGN expr SEMI | IF expr THEN stat_seq else_stat ENDIF SEMI | WHILE expr DO stat_seq ENDDO SEMI | FOR ID ASSIGN expr TO expr DO stat_seq ENDDO SEMI |
    opt_prefix ID LPAREN expr_list RPAREN SEMI | BREAK | RETURN expr | LET declaration_segment IN stat_seq END SEMI;
l_tail : ASSIGN lvalue l_tail | NULL;

else_stat : ELSE stat_seq | NULL;

opt_prefix : lvalue ASSIGN | NULL;

// <expr> is our “<or-term>”
expr : and_term e_tail;
e_tail : OR and_term e_tail | NULL;

and_term : greater_eq_term and_tail;
and_tail : AND greater_eq_term and_tail | NULL;

greater_eq_term : less_eq_term greater_eq_tail;
greater_eq_tail : GREATEREQ less_eq_term greater_eq_tail | NULL;

less_eq_term : greater_term less_eq_tail;
less_eq_tail : LESSEREQ greater_term less_eq_tail | NULL;

greater_term : less_term greater_tail;
greater_tail : GREATER less_term greater_tail | NULL;

less_term : not_eq_term less_tail;
less_tail : LESSER not_eq_term less_tail | NULL;

not_eq_term : eq_term not_eq_tail;
not_eq_tail : NEQ eq_term not_eq_tail | NULL;


eq_term : div_term eq_tail;
eq_tail : EQ div_term eq_tail | NULL;


div_term : mult_term div_tail;
div_tail : DIV mult_term div_tail | NULL;

mult_term : sub_term mult_tail;
mult_tail : MULT sub_term mult_tail | NULL;

sub_term : add_term sub_tail;
sub_tail : MINUS add_term sub_tail | NULL; 

add_term : pow_term add_tail;
add_tail : PLUS pow_term add_tail | NULL;


pow_term : factor pow_tail;
pow_tail : EXP factor pow_tail | NULL;

factor : LPAREN expr RPAREN | const |  lvalue;

const : INTLIT | FLOATLIT;

expr_list : expr expr_list_tail | NULL;
expr_list_tail : COMMA expr expr_list_tail | NULL;
lvalue : ID lvalue_tail;
lvalue_tail : RBRACK expr LBRACK | NULL;
