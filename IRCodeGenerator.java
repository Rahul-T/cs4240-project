// Generated from tiger.g4 by ANTLR 4.7.1
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.Token;
import java.util.Arrays;

/**
 * This class provides an empty implementation of {@link tigerVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <String> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public class IRCodeGenerator extends tigerBaseVisitor<String> {
    private SymbolTable symTable;
    private int tempCount = 0;
    private int labelCount = 0;

    public IRCodeGenerator() {
        System.out.println("\nIR GENERATED CODE:");
        this.symTable = new SymbolTable();
    }

    public void emit(String s) {
        System.out.println(s);
    }

    public String newTemp() {
        tempCount++;
        return "t" + String.valueOf(tempCount);
    }

    public String newLabel() {
        labelCount++;
        return "l" + String.valueOf(labelCount);
    }

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
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

        if (typeArr.length == 1) {
            this.symTable.addType(id, type);
        } else {
            this.symTable.addArray(id, "type", typeArr[2], Integer.parseInt(typeArr[1]));
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
        if(ctx.getChildCount() == 1) {
            return ctx.getChild(0).getText();
        } else {
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
        String initValue = visit(ctx.getChild(4));

        if(typeInfo.length > 1) {
            for(String id: idList) {
                symTable.addArray(id, "var" ,typeInfo[2], Integer.parseInt(typeInfo[1]));
                if(initValue != null) {
                    emit("assign " + id + ", " + Integer.parseInt(typeInfo[1]) + ", " + initValue);
                }
            }
        } else {
            for(String id: idList) {
                symTable.addVariable(id, typeInfo[0]);
                if(initValue != null) {
                    emit("assign " + id + ", " + initValue);
                }
            }
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
        // optional_init : ASSIGN constant | /* NULL */;
        if(ctx.getChildCount() == 0) {
            return null;
        }
        return ctx.getChild(1).getText();
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
        String actualRetType = visit(ctx.getChild(7));
        symTable.closeScope();
        String[] paramTypeArr = paramTypeString.split(",");

        // First one: Func Name
        // Second one: Ret Type
        String id = ctx.getChild(1).getText();
        String declaredRetType = visit(ctx.getChild(5));
        symTable.addFunction(id, declaredRetType, paramTypeArr);

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
        // param_list_tail : COMMA param param_list_tail | /* NULL */;
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
        return visitChildren(ctx);
    }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitStat_seq(tigerParser.Stat_seqContext ctx) {
        visitChildren(ctx);
        return "";
    }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitStat_seq_tail(tigerParser.Stat_seq_tailContext ctx) {
        visitChildren(ctx);
        return "";
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
        // System.out.println("statement");
        switch(ctx.getChild(0).getText()) {
            case "if":
                visit(ctx.getChild(1));
                visit(ctx.getChild(3));
                visit(ctx.getChild(4));
                break;
            case "while":
                visit(ctx.getChild(1));
                visit(ctx.getChild(3));
                break;
            case "for":
                String id = ctx.getChild(1).getText();
                String expr1 = visit(ctx.getChild(3));
                emit("assign " + id + ", " + expr1);
                break;
            case "break":
                break;
            case "return":
                String retVal = visit(ctx.getChild(1));
                emit("return " + retVal);
                break;
            case "let":
                symTable.openScope();
                visit(ctx.getChild(1));
                visit(ctx.getChild(3));
                symTable.closeScope();
                break;
            default:
                String currId = ctx.getChild(0).getText();
                // System.out.println(currId);
                String idTail = visit(ctx.getChild(1));
                String[] params = idTail.split(" ");
                boolean isAssign = ctx.getChild(1).getText().substring(0,2).equals(":=");
                boolean isArray = symTable.lookupSymbol(currId).isArray();
                // System.out.println(isArray);
                // System.out.println(isAssign);
                // System.out.println(Arrays.toString(params));
                if(params.length > 1) {
                    if(isAssign) {
                        String allParams = "";
                        for(int i=1; i<params.length; i++) {
                            allParams += ", " + params[i];
                        }
                        emit("callr, " + currId + ", " + params[0] + allParams);
                    } else {
                        String allParams = "";
                        for(int i=1; i<params.length; i++) {
                            allParams += ", " + params[i];
                        }
                        emit("call, " + currId + ", " + params[0] + allParams);
                    }
                } else {
                    //Unreachable;
                    if(!isAssign && !isArray) {
                        System.out.println("Should be unreachable");
                    }
                    //Regular assign "F := 2"
                    else if(isAssign && !isArray && !params[0].contains(";")) {
                        emit("assign, " + currId + ", " + params[0]);
                    }
                    // Assigning indexed array val to non-array "F := C[2]"
                    else if(isAssign && !isArray && params[0].contains(";")) {
                        String[] arrayInfo = params[0].split(";");
                        emit("load, " + currId + ", " + arrayInfo[0] + ", " + arrayInfo[1]);
                    }

                    // Assigning array to array "C := C"
                    else if(isAssign && isArray) {
                        emit("assign, " + currId + ", " + params[0]);
                    }
                    else {
                        String[] moreArrayInfo = params[0].split(";");
                        // Assigning val to indexed array "C[1] := 2"
                        if(!isAssign && moreArrayInfo.length == 2) {
                            emit("store, " + currId + ", " + moreArrayInfo[0] + ", " + moreArrayInfo[1]);
                        }
                        // Assigning indexed array val to indexed array "C[1] := ZZ[2]"
                        else if(!isAssign && moreArrayInfo.length == 3) {
                            String tmp = newTemp();
                            emit("load, " + tmp + ", " + moreArrayInfo[1] + ", " + moreArrayInfo[2]);
                            emit("store, " + currId + ", " + moreArrayInfo[0] + ", " + tmp);
                        } else {
                            System.out.println("Should be unreachable");
                        }
                    }
                }
                

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
                return visit(ctx.getChild(1)) + ";" + visit(ctx.getChild(4));
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
            String params = visit(ctx.getChild(2));
            //symTable.getType(ctx.getStart().getText());
            return ctx.getStart().getText() + " " + params;
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
        String id = ctx.getChild(0).getText();
        String lvalueTail = visit(ctx.getChild(1));
        if(lvalueTail != null) {
            id += ";" + lvalueTail;
        }
        
        return id;
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
        return visit(ctx.getChild(1));
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
        String andTermVal = visit(ctx.getChild(0));
        String eTailVal = visit(ctx.getChild(1));
        if(eTailVal == null) {
            return andTermVal;
        }
        String tmp = newTemp();
        emit("and " + andTermVal + ", " + eTailVal + ", " + tmp);

        return tmp;
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

        if (ctx.getChildCount() == 0)
            return null;

        String andTermVal = visit(ctx.getChild(1));
        String eTailVal = visit(ctx.getChild(2));

        if(eTailVal == null) {
            return andTermVal;
        }
        String tmp = newTemp();
        emit("or " + andTermVal + ", " + eTailVal + ", " + tmp);

        return tmp;
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

        String compTermVal = visit(ctx.getChild(0));
        String andTailVal = visit(ctx.getChild(1));

        if(andTailVal == null) {
            return compTermVal;
        }
        String tmp = newTemp();
        emit("and " + compTermVal + ", " + andTailVal + ", " + tmp);

        return tmp;
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
        if (ctx.getChildCount() == 0)
            return null;

        String compTermVal = visit(ctx.getChild(1));
        String andTailVal = visit(ctx.getChild(2));

        if(andTailVal == null) {
            return compTermVal;
        }
        String tmp = newTemp();
        emit("and " + compTermVal + ", " + andTailVal + ", " + tmp);

        return tmp;
    }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitComparison_term(tigerParser.Comparison_termContext ctx) {
        // comparison_term: div_term comparison_tail;

        String divTermVal = visit(ctx.getChild(0));
        String compTailVal = visit(ctx.getChild(1));

        if(compTailVal == null) {
            return divTermVal;
        }

        String tmp = newTemp();

        emit("comp " + divTermVal + ", " + compTailVal + ", " + tmp);

        return tmp;
    }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitComparison_tail(tigerParser.Comparison_tailContext ctx) {
        // comparison_tail : (EQ | NEQ | LESSER | LESSEREQ | GREATER | GREATEREQ) div_term | /* NULL */;
        
        if (ctx.getChildCount() == 0)
            return null;

        String divTermVal = visit(ctx.getChild(1));

        return divTermVal;
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

        String multTermVal = visit(ctx.getChild(0));
        String divTailVal = visit(ctx.getChild(1));

        if(divTailVal == null) {
            return multTermVal;
        }
        String tmp = newTemp();
        emit("div " + multTermVal + ", " + divTailVal + ", " + tmp);

        return tmp;
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

        String multTermVal = visit(ctx.getChild(1));
        String divTailVal = visit(ctx.getChild(2));

        if(divTailVal == null) {
            return multTermVal;
        }
        String tmp = newTemp();
        emit("div " + multTermVal + ", " + divTailVal + ", " + tmp);

        return tmp;
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

        String subTermVal = visit(ctx.getChild(0));
        String multTailVal = visit(ctx.getChild(1));

        if(multTailVal == null) {
            return subTermVal;
        }
        String tmp = newTemp();
        emit("mult " + subTermVal + ", " + multTailVal + ", " + tmp);

        return tmp;
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

        String subTermVal = visit(ctx.getChild(1));
        String multTailVal = visit(ctx.getChild(2));

        if(multTailVal == null) {
            return subTermVal;
        }
        String tmp = newTemp();
        emit("mult " + subTermVal + ", " + multTailVal + ", " + tmp);

        return tmp;
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

        String addTermVal = visit(ctx.getChild(0));
        String subTailVal = visit(ctx.getChild(1));

        if(subTailVal == null) {
            return addTermVal;
        }
        String tmp = newTemp();
        emit("sub " + addTermVal + ", " + subTailVal + ", " + tmp);

        return tmp;
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

        String addTermVal = visit(ctx.getChild(1));
        String subTailVal = visit(ctx.getChild(2));

        if(subTailVal == null) {
            return addTermVal;
        }
        String tmp = newTemp();
        emit("sub " + addTermVal + ", " + subTailVal + ", " + tmp);

        return tmp;
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

        String powTermVal = visit(ctx.getChild(0));
        String addTailVal = visit(ctx.getChild(1));
        if(addTailVal == null) {
            return powTermVal;
        }
        String tmp = newTemp();
        emit("add " + powTermVal + ", " + addTailVal + ", " + tmp);

        return tmp;
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

        String powTermVal = visit(ctx.getChild(1));
        String addTailVal = visit(ctx.getChild(2));

        if(addTailVal == null) {
            return powTermVal;
        }
        String tmp = newTemp();
        emit("add " + powTermVal + ", " + addTailVal + ", " + tmp);

        return tmp;
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
        String factorVal = visit(ctx.getChild(0));
        String powTailVal = visit(ctx.getChild(1));
        if(powTailVal == null) {
            return factorVal;
        }
        String tmp = newTemp();
        emit("pow " + factorVal + ", " + powTailVal + ", " + tmp);
        return tmp;
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

        String factorVal = visit(ctx.getChild(1));
        String powTailVal = visit(ctx.getChild(2));

        if(powTailVal == null) {
            return factorVal;
        }
        String tmp = newTemp();
        emit("pow " + factorVal + ", " + powTailVal + ", " + tmp);

        return tmp;
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

        switch(ctx.getStart().getType()) {
            case 5: // LPAREN
                return visit(ctx.getChild(1));
            case 50: // lvalue / ID
                return visit(ctx.getChild(0));
            case 51: // constant / INTLIT
            case 52: // constant / FLOATLIT
                return ctx.getChild(0).getText();
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
        String expr = visit(ctx.getChild(0));
        String exprList = visit(ctx.getChild(1));
        return expr + " " + exprList;
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
        String expr = visit(ctx.getChild(1));
        String exprList = visit(ctx.getChild(2));
        return expr + " " + exprList;
    }
}