import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.Token;
import java.util.Arrays;

public class SemanticChecker extends tigerBaseVisitor<String> {
    private SymbolTable symTable;

    public void printSymbolTable() {
        System.out.println(symTable);
    }

    private void symbolTableError(ParserRuleContext ctx, int index, String id, String err) {
        // ParserRuleContext prc = (ParserRuleContext) ctx;
        Token errToken = ctx.getToken(50, index).getSymbol(); //token 50 is the id
        System.out.println("\nCOMPLIATION ERROR! Error while building symbol table at line " 
            + String.valueOf(errToken.getLine()) + " character " + String.valueOf(errToken.getCharPositionInLine()));

        switch(err) {
            default:
                System.out.println("- " + id + " " + err);
        }
        System.exit(1);
    }

    private void symbolTableError(ParserRuleContext ctx, int index, String err) {
        // ParserRuleContext prc = (ParserRuleContext) ctx;

        Token errToken = ctx.getToken(50, index).getSymbol(); //token 50 is the id
        System.out.println("\nCOMPLIATION ERROR! Error while building symbol table at line " 
            + String.valueOf(errToken.getLine()) + " character " + String.valueOf(errToken.getCharPositionInLine()));

        switch(err) {
            default:
                System.out.println("- " + errToken.getText() + " " + err);
        }
        System.exit(1);
    }

    private void semanticError(Token errToken, String err) {
        System.out.println("\nSEMANTIC ERROR! Error at " + errToken.getText() + " in line " 
            + String.valueOf(errToken.getLine()) + " character "
            + String.valueOf(errToken.getCharPositionInLine()) + " " + err);
        System.exit(1);
    }

    private String validateTypes(String type1, String type2, ParserRuleContext ctx, String operator) {
        if (type1 == null && type2 == null) {
            return null;
        } else if (type1 == null) {
            return type2;
        } else if (type2 == null) {
            return type1;
        } else if(type1.equals(type2)) {
            return type1;
        }
        else if((type1.equals("float") && type2.equals("int")) 
                || (type2.equals("float") && type1.equals("int"))) {
            return "float";
        }
        semanticError(ctx.getStart(), "invalid types for " + operator +" operator");
        return null;
    }

    public SemanticChecker() {
        this.symTable = new SymbolTable();
    }

    @Override
    public String visitTiger_program(tigerParser.Tiger_programContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitDeclaration_segment(tigerParser.Declaration_segmentContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitType_declaration_list(tigerParser.Type_declaration_listContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitVar_declaration_list(tigerParser.Var_declaration_listContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitFunction_declaration_list(tigerParser.Function_declaration_listContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitType_declaration(tigerParser.Type_declarationContext ctx) {
        // type_declaration : TYPE ID EQUALS type SEMI;

        String type = visit(ctx.getChild(3));
        String id = ctx.getChild(1).getText();
        String[] typeArr = type.split(" ");
        boolean success;

        if (typeArr.length == 1)
            success = this.symTable.addType(id, type);
        else {
            success = this.symTable.addArray(id, "type", typeArr[2], Integer.parseInt(typeArr[1]));
        }

        if (!success) {
            symbolTableError(ctx, 1, "previously declared");
        }
        return "";
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitType(tigerParser.TypeContext ctx) {
        // type : ARRAY LBRACK INTLIT RBRACK OF type_id | ID | type_id;

        int childCount = ctx.getChildCount();
        switch(childCount) {
            case 0:
                // check for nulll
                return "";
            case 1:
                // if it's just an int, float, or ID
                return ctx.getChild(0).getText();
            default:
                // if it's an array, we need the type
                return ctx.getChild(0).getText() + " " + ctx.getChild(2).getText()
                    + " " + ctx.getChild(5).getText();
        }
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitType_id(tigerParser.Type_idContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitVar_declaration(tigerParser.Var_declarationContext ctx) {
        // var_declaration : VAR id_list COLON type optional_init SEMI; 
        String ids = visit(ctx.getChild(1));
        String[] idList = ids.split(" ");
        String[] typeInfo = visit(ctx.getChild(3)).split(" ");
        boolean success;
        if(typeInfo.length > 1) {
            // type is array
            for(String id: idList) {
                success = symTable.addArray(id, "var" ,typeInfo[2], Integer.parseInt(typeInfo[1]));
                if (!success)
                    symbolTableError(ctx, 1, id, "previously declared");
            }
        } else {
            for(String id: idList) {
                success = symTable.addVariable(id, typeInfo[0]);
                if (!success)
                    symbolTableError(ctx, 1, id, "previously declared");
            }
        }

        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitId_list(tigerParser.Id_listContext ctx) {
        // id_list : ID id_list_tail;
        String id = ctx.getChild(0).getText();
        String idListTail = visit(ctx.getChild(1));
        return id + " " + idListTail;
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitId_list_tail(tigerParser.Id_list_tailContext ctx) {
        // id_list_tail : COMMA ID id_list_tail | /* NULL */;
        if(ctx.getChildCount() == 0) {
            return "";
        }
        String id = ctx.getChild(1).getText();
        String idListTail = visit(ctx.getChild(2));
        return id + " " + idListTail;
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitOptional_init(tigerParser.Optional_initContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitFunction_declaration(tigerParser.Function_declarationContext ctx) {
        // function_declaration : FUNC ID LPAREN param_list RPAREN ret_type BEGIN stat_seq END SEMI;
        symTable.openScope();
        String paramTypeString = visit(ctx.getChild(3));
        symTable.closeScope();
        String[] paramTypeArr = paramTypeString.split(",");
        
        // First one: Func Name
        // Second one: Ret Type
        boolean success = symTable.addFunction(ctx.getChild(1).getText(),
            ctx.getChild(5).getChild(1).getText(),
            paramTypeArr);
            
        if (!success)
            symbolTableError(ctx, 1, "previously declared");

        return "";
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitParam_list(tigerParser.Param_listContext ctx) {
        //param_list : param param_list_tail | /* NULL */;
        if (ctx.getChildCount() == 0) {
            return "";
        }

        return visit(ctx.getChild(0)) + visit(ctx.getChild(1));
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitParam_list_tail(tigerParser.Param_list_tailContext ctx) {
        // //param_list_tail : COMMA param param_list_tail | /* NULL */;
        if (ctx.getChildCount() == 0) {
            return "";
        }
        return  "," + visit(ctx.getChild(1)) + visit(ctx.getChild(2));
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitRet_type(tigerParser.Ret_typeContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitParam(tigerParser.ParamContext ctx) {
        // param : ID COLON type;
        if (!this.symTable.addVariable(ctx.getChild(0).getText(), visit(ctx.getChild(2))))
            symbolTableError(ctx, 0, "previously declared");
        return ctx.getChild(2).getText();
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitStat_seq(tigerParser.Stat_seqContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitStat_seq_tail(tigerParser.Stat_seq_tailContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitStat(tigerParser.StatContext ctx) {
        /*
        stat :  IF expr THEN stat_seq else_stat ENDIF SEMI |
        WHILE expr DO stat_seq ENDDO SEMI |
        FOR ID ASSIGN expr TO expr DO stat_seq ENDDO SEMI |
        BREAK SEMI |
        RETURN expr SEMI |
        LET declaration_segment IN stat_seq END SEMI |
        ID id_tail;
        */
        String exprType;
        switch(ctx.getChild(0).getText()) {
            case "if":
                exprType = visit(ctx.getChild(1));
                if (!exprType.equals("int"))
                    semanticError(ctx.getStart(), "Conditional must evaluate to integer!");
                visit(ctx.getChild(3));
                visit(ctx.getChild(4));
                break;
            case "while":
                exprType = visit(ctx.getChild(1));
                if (!exprType.equals("int"))
                    semanticError(ctx.getStart(), "Conditional must evaluate to integer!");
                visit(ctx.getChild(3));
                break;
            case "for":
                if(!symTable.containsSymbol(ctx.getChild(1).getText())) {
                    symbolTableError(ctx, 1, "undeclared");
                } else {
                    String idType = symTable.lookupSymbol(ctx.getChild(1).getText()).getType();
                    String expr1Type = visit(ctx.getChild(3));
                    String expr2Type = visit(ctx.getChild(5));
                    if (!idType.equals("int") || !expr1Type.equals("int") || !expr2Type.equals("int"))
                        semanticError(ctx.getStart(), "For loop variables must be integers!");
                    visit(ctx.getChild(7));
                }
                break;
            case "break":
                break;
            case "return":
                break;
            case "let":
                break;
            default: // the ID id_tail
                String id = ctx.getChild(0).getText();
                if(!symTable.containsSymbol(id)) {
                    symbolTableError(ctx, 0, "undeclared");
                } else {
                    String idTailType = visit(ctx.getChild(1));
                    String[] paramTypes = idTailType.split(" ");
                    if(paramTypes.length > 1) {
                        String[] idParams = symTable.lookupSymbol(id).getParamList();
                        if(idParams.length != paramTypes.length) {
                            semanticError(ctx.getStart(), "Incorrect number of params for function " + id);
                        }
                        for(int i=0; i<idParams.length; i++) {
                            if(!idParams[i].equals(paramTypes[i])) {
                                semanticError(ctx.getStart(), "Type mismatch in params for function " + id
                                                + ". Expected " + idParams[i] + " got " + paramTypes[i]);
                            }
                        }
                    }

                    String idType = symTable.getType(ctx.getChild(0).getText());
                    boolean isAssign = ctx.getChild(1).getText().substring(0,2).equals(":=");
                    boolean isArray = idTailType.contains("array");

                    if(isArray) {
                        idTailType = idTailType.substring(0, idTailType.length() - 5);
                    }
                     if(isAssign && !idType.equals(idTailType)) {
                        semanticError(ctx.getStart(), "Type mismatch! Expected " + idType + ". Got " + idTailType + ".");
                    }
                    
                    // return idType;
                }
                
        }

        return null;
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitId_tail(tigerParser.Id_tailContext ctx) {
        /*
            id_tail :   ASSIGN assign_tail |
                        LBRACK expr RBRACK ASSIGN expr SEMI |
                        LPAREN expr_list RPAREN SEMI;
        */

        switch (ctx.getStart().getType()) {
            case 26: // assign
                return visit(ctx.getChild(1));
            case 7: // lbrack
                String expr1type = visit(ctx.getChild(1));
                if(!expr1type.equals("int")) {
                    semanticError(ctx.getStart(), "Array must be indexed with int");
                }
                return visit(ctx.getChild(4));
            case 5: // lparen
                return visit(ctx.getChild(1));
        }
        return null;
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitAssign_tail(tigerParser.Assign_tailContext ctx) {
        // assign_tail: expr SEMI | ID LPAREN expr_list RPAREN SEMI;
        if(ctx.getChild(1).getText().equals("(")) {
            String id = ctx.getChild(0).getText();
            SymbolData sd = symTable.lookupSymbol(id);
            if(sd == null) {
                symbolTableError(ctx, 0, "undeclared");
            } else if (!sd.getClassification().equals("func")) {
                symbolTableError(ctx, 0, "is not a function");
            }
            String[] idParams = symTable.lookupSymbol(id).getParamList();
            String[] paramTypes = visit(ctx.getChild(2)).split(" ");
            if(idParams.length != paramTypes.length) {
                semanticError(ctx.getStart(), "Incorrect number of params for function " + id);
            }
            for(int i=0; i<idParams.length; i++) {
                if(!idParams[i].equals(paramTypes[i])) {
                    semanticError(ctx.getStart(), "Type mismatch in params for function " + id
                                    + ". Expected " + idParams[i] + " got " + paramTypes[i]);
                }
            }

            return symTable.getType(ctx.getStart().getText());
        }

        return visit(ctx.getChild(0));
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitLvalue(tigerParser.LvalueContext ctx) {
        // <lvalue> → id <lvalue-tail>
        if(!symTable.containsSymbol(ctx.getChild(0).getText())) {
            symbolTableError(ctx, 0, "undeclared");
        }
        String symbol = ctx.getChild(0).getText();
        String type = symTable.getType(symbol);

        // Hacky fix
        if(ctx.getChildCount() > 1 && !ctx.getChild(1).getText().equals("")) {
            if (symTable.isArray(symbol)) {
                type += "array";
                visit(ctx.getChild(1));
            } else {
                semanticError(ctx.getStart(), "Can't index into non-array type");
            }
        }
        return type;
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitLvalue_tail(tigerParser.Lvalue_tailContext ctx) {
        // lvalue_tail : LBRACK expr RBRACK | /* NULL */;
        if(ctx.getChildCount() == 0) {
            return null;
        }
        String exprType = visit(ctx.getChild(1));
        if(exprType == null || !exprType.equals("int")) {
            semanticError(ctx.getStart(), "Array must be indexed with int");
        }
        return exprType;
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitElse_stat(tigerParser.Else_statContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitExpr(tigerParser.ExprContext ctx) {
        // expr : and_term e_tail;

        String andType = visit(ctx.getChild(0));
        String eTailType = visit(ctx.getChild(1));

        if (eTailType == null)
            return andType;
        else if (!andType.equals("int") || !eTailType.equals("int"))
            semanticError(ctx.getStart(), "Logical operands must be of type int!");
        return "int";
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitE_tail(tigerParser.E_tailContext ctx) {
        // e_tail : OR and_term e_tail | /* NULL */;
        if (ctx.getChildCount() == 0) {
            return null;
        } else {
            String andType = visit(ctx.getChild(1));
            if (!andType.equals("int"))
                semanticError(ctx.getStart(), "Logical operand must be of type int!");
            String eTailType = visit(ctx.getChild(2));
            if (eTailType != null && !eTailType.equals("int"))
                semanticError(ctx.getStart(), "Logical operand must be of type int!");
            return "int";
        }
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitAnd_term(tigerParser.And_termContext ctx) {
        // and_term : comparison_term and_tail;
        String compType = visit(ctx.getChild(0));
        String andType = visit(ctx.getChild(1));

        if (andType == null)
            return compType;
        else if (!compType.equals("int") || !andType.equals("int"))
            semanticError(ctx.getStart(), "Logical operands must be of type int!");
        return "int";
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitAnd_tail(tigerParser.And_tailContext ctx) {
        // and_tail : AND comparison_term and_tail | /* NULL */;
        if (ctx.getChildCount() == 0) {
            return null;
        } else {
            String compType = visit(ctx.getChild(1));
            if (!compType.equals("int"))
                semanticError(ctx.getStart(), "Logical operand must be of type int!");
            String andTailType = visit(ctx.getChild(2));
            if (andTailType != null && !andTailType.equals("int"))
                semanticError(ctx.getStart(), "Logical operand must be of type int!");
            return "int";
        }
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitComparison_term(tigerParser.Comparison_termContext ctx) {
        // eq_term : div_term eq_tail;
        String type1 = visit(ctx.getChild(0));
        String type2 = visit(ctx.getChild(1));

        if (type2 == null)
            return type1;
        else if (type1.equals(type2))
            return "int";
        else 
            semanticError(ctx.getStart(), "Comparison operands must have same type!");

        return null;
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitComparison_tail(tigerParser.Comparison_tailContext ctx) {
        // eq_tail : EQ div_term | /* NULL */
        if (ctx.getChildCount() == 0)
            return null;
        return visit(ctx.getChild(1));
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitDiv_term(tigerParser.Div_termContext ctx) {
        // div_term : mult_term div_tail;
        String divType = visit(ctx.getChild(0));
        String tailType = visit(ctx.getChild(1));
        return validateTypes(divType, tailType, ctx, "/");
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitDiv_tail(tigerParser.Div_tailContext ctx) {
        // div_tail : PLUS mult_term div_tail | /* NULL */;
        if (ctx.getChildCount() == 0)
            return null;
        String divType = visit(ctx.getChild(1));
        String tailType = visit(ctx.getChild(2));
        return validateTypes(divType, tailType, ctx, "/");
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitMult_term(tigerParser.Mult_termContext ctx) {
        // mult_term : sub_term mult_tail;
        String multType = visit(ctx.getChild(0));
        String tailType = visit(ctx.getChild(1));
        return validateTypes(multType, tailType, ctx, "*");
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitMult_tail(tigerParser.Mult_tailContext ctx) {
        // mult_tail : PLUS sub_term mult_tail | /* NULL */;
        if (ctx.getChildCount() == 0)
            return null;
        String multType = visit(ctx.getChild(1));
        String tailType = visit(ctx.getChild(2));
        return validateTypes(multType, tailType, ctx, "*");
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitSub_term(tigerParser.Sub_termContext ctx) {
        // sub_term : add_term sub_tail;
        String subType = visit(ctx.getChild(0));
        String tailType = visit(ctx.getChild(1));
        return validateTypes(subType, tailType, ctx, "-");
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitSub_tail(tigerParser.Sub_tailContext ctx) {
        // sub_tail : PLUS add_term sub_tail | /* NULL */;
        if (ctx.getChildCount() == 0)
            return null;
        String subType = visit(ctx.getChild(1));
        String tailType = visit(ctx.getChild(2));
        return validateTypes(subType, tailType, ctx, "-");
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitAdd_term(tigerParser.Add_termContext ctx) {
        // add_term : pow_term add_tail;
        String powType = visit(ctx.getChild(0));
        String tailType = visit(ctx.getChild(1));
        return validateTypes(powType, tailType, ctx, "+");
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitAdd_tail(tigerParser.Add_tailContext ctx) {
        // add_tail : PLUS pow_term add_tail | /* NULL */;
        if (ctx.getChildCount() == 0)
            return null;
        String addType = visit(ctx.getChild(0));
        String tailType = visit(ctx.getChild(1));
        return validateTypes(addType, tailType, ctx, "+");
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitPow_term(tigerParser.Pow_termContext ctx) {
        // pow_term : factor pow_tail;
        String tailType = visit(ctx.getChild(1));
        String type = visit(ctx.getChild(0));
        if(tailType == null) {
            return type;
        }
        
        if(!(type.equals("int") || type.equals("float"))) {
            System.out.println(type);
            semanticError(ctx.getStart(), "Power operator must be int or float");
        }
        return type;
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitPow_tail(tigerParser.Pow_tailContext ctx) {
        // pow_tail : EXP factor pow_tail | /* NULL */;

        if (ctx.getChildCount() == 0)
            return null;

        visit(ctx.getChild(2));

        String childType = visit(ctx.getChild(1));
        String symbolName = ctx.getChild(1).getText();
        // System.out.println("Child: " + ctx.getChild(1).getText() + " Type: " + childType);
        if (!childType.equals("int")) {
            semanticError(ctx.getStart(), "Exponent must be int");
        }
        return "int";
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitFactor(tigerParser.FactorContext ctx) {
        // factor : LPAREN expr RPAREN | constant |  lvalue;
        // System.out.println(String.valueOf(ctx.getStart().getType()));
        switch(ctx.getStart().getType()) {
            case 5: // LPAREN
                return visit(ctx.getChild(1));
            case 50: // lvalue / ID
                return visit(ctx.getChild(0));
            case 51: // constant / INTLIT
                return "int";
            case 52: // constant / FLOATLIT
                return "float";
        }
        
        return null;
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitConstant(tigerParser.ConstantContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitExpr_list(tigerParser.Expr_listContext ctx) {
        // expr_list : expr expr_list_tail | /* NULL */;
        if(ctx.getChildCount() == 0) {
            return "";
        }
        String exprType = visit(ctx.getChild(0));
        String exprListType = visit(ctx.getChild(1));
        return exprType + " " + exprListType;
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitExpr_list_tail(tigerParser.Expr_list_tailContext ctx) {
        // expr_list_tail : COMMA expr expr_list_tail | /* NULL */;
        if(ctx.getChildCount() == 0) {
            return "";
        }
        String exprType = visit(ctx.getChild(1));
        String exprListType = visit(ctx.getChild(2));
        return exprType + " " + exprListType;
    }
}