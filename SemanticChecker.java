import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.Token;

public class SemanticChecker extends tigerBaseVisitor<String> {
    private SymbolTable symTable;

    public void printSymbolTable() {
        System.out.println(symTable);
    }

    private void symbolTableError(ParseTree ctx, String id, String err) {
        ParserRuleContext prc = (ParserRuleContext) ctx;
        Token errToken = prc.getStart();
        System.out.println("\nCOMPLIATION ERROR! Error while building symbol table at line " 
            + String.valueOf(errToken.getLine()) + " character " + String.valueOf(errToken.getCharPositionInLine()));

        switch(err) {
            default:
                System.out.println("- " + id + " " + err);
        }
        System.exit(1);
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
            symbolTableError(ctx.getChild(1), id, "previously declared");
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
                    symbolTableError(ctx.getChild(1), id, "previously declared");
            }
        } else {
            for(String id: idList) {
                success = symTable.addVariable(id, typeInfo[0]);
                if (!success)
                    symbolTableError(ctx.getChild(1), id, "previously declared");
            }
        }


        // System.out.println(idList);
        // System.out.println(type);
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
            symbolTableError(ctx.getChild(1), ctx.getChild(1).getText(), "previously declared");

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
            symbolTableError(ctx.getChild(0), ctx.getChild(0).getText(), "previously declared");
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

        switch(ctx.getChild(0).getText()) {
            case "if":
                break;
            case "while":
                break;
            case "for":
                if(!symTable.containsSymbol(ctx.getChild(1).getText())) {
                    symbolTableError(ctx.getChild(1), ctx.getChild(1).getText(), "undeclared");
                }
                break;
            case "break":
                break;
            case "return":
                break;
            case "let":
                break;
            default:
                if(!symTable.containsSymbol(ctx.getChild(0).getText())) {
                    symbolTableError(ctx.getChild(0), ctx.getChild(0).getText(), "undeclared");
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
    public String visitId_tail(tigerParser.Id_tailContext ctx) {
        return visitChildren(ctx);
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
            System.out.println("symbol: " + id + "sd: " + sd);
            if(sd == null) {
                symbolTableError(ctx, id, "undeclared");
            } else if (!sd.getClassification().equals("func")) {
                symbolTableError(ctx, id, "is not a function");
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
    public String visitLvalue(tigerParser.LvalueContext ctx) {
        // <lvalue> → id <lvalue-tail>
        if(!symTable.containsSymbol(ctx.getChild(0).getText())) {
            // System.out.println(ctx.getChild(0).getText());
            symbolTableError(ctx.getChild(0), ctx.getChild(0).getText(), "undeclared");
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
    public String visitLvalue_tail(tigerParser.Lvalue_tailContext ctx) {
        return visitChildren(ctx);
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
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitE_tail(tigerParser.E_tailContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitAnd_term(tigerParser.And_termContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitAnd_tail(tigerParser.And_tailContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitGreater_eq_term(tigerParser.Greater_eq_termContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitGreater_eq_tail(tigerParser.Greater_eq_tailContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitLess_eq_term(tigerParser.Less_eq_termContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitLess_eq_tail(tigerParser.Less_eq_tailContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitGreater_term(tigerParser.Greater_termContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitGreater_tail(tigerParser.Greater_tailContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitLess_term(tigerParser.Less_termContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitLess_tail(tigerParser.Less_tailContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitNot_eq_term(tigerParser.Not_eq_termContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitNot_eq_tail(tigerParser.Not_eq_tailContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitEq_term(tigerParser.Eq_termContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitEq_tail(tigerParser.Eq_tailContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitDiv_term(tigerParser.Div_termContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitDiv_tail(tigerParser.Div_tailContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitMult_term(tigerParser.Mult_termContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitMult_tail(tigerParser.Mult_tailContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitSub_term(tigerParser.Sub_termContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitSub_tail(tigerParser.Sub_tailContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitAdd_term(tigerParser.Add_termContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitAdd_tail(tigerParser.Add_tailContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitPow_term(tigerParser.Pow_termContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitPow_tail(tigerParser.Pow_tailContext ctx) {
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitFactor(tigerParser.FactorContext ctx) {
        return visitChildren(ctx);
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
        return visitChildren(ctx);
    }
    
    /**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitExpr_list_tail(tigerParser.Expr_list_tailContext ctx) {
        return visitChildren(ctx);
    }
}