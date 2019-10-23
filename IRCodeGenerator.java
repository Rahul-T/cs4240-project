// Generated from tiger.g4 by ANTLR 4.7.1
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.Token;
import java.util.Arrays;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.*;

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
    private boolean verboseOutput;
    private PrintWriter outWriter;
    private Stack<String> labelStack;

    public IRCodeGenerator(String outFileName, boolean verboseOutput) throws IOException {
        this.labelStack = new Stack<String>();
        this.symTable = new SymbolTable();
        this.verboseOutput = verboseOutput;
        this.outWriter = (outFileName == null) ? null : new PrintWriter(new FileWriter(outFileName));
        if (this.verboseOutput)
            System.out.println("\nIR GENERATED CODE:\n");
    }

    public IRCodeGenerator(boolean verboseOutput) throws IOException {
        this(null, verboseOutput);
    }

    public void emit(String s) {
        if (this.verboseOutput)
            System.out.println(s);
        if (this.outWriter != null)
            this.outWriter.print(s + "\n");
    }

    public void closeOutput() {
        if (this.outWriter != null)
            this.outWriter.close();
    }

    public String newTemp() {
        tempCount++;
        return "t" + String.valueOf(tempCount);
    }

    public String newLabel() {
        labelCount++;
        return "label" + String.valueOf(labelCount);
    }

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
    @Override
    public String visitTiger_program(tigerParser.Tiger_programContext ctx) {
        emit("# start_function main");
        String out = visitChildren(ctx);
        emit("# end_function main");
        this.closeOutput();
        return out;
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
        emit("# start_function " + ctx.getChild(1).getText());
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
        emit("# end_function " + ctx.getChild(1).getText());

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
        // param : ID COLON type;
        this.symTable.addVariable(ctx.getChild(0).getText(), visit(ctx.getChild(2)));
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
            case "if": // IF expr THEN stat_seq else_stat ENDIF SEMI

                String ifCondExpr = visit(ctx.getChild(1)).split(" ")[0];
                /*
                    condExpr = visit(ctx.getChild(1));
                    breq condExpr, 0, elseLabel
                    visit(ctx.getChild(3));
                    goto ifendLabel
                    *elseLabel*
                    visit(ctx.getChild(4));
                    *ifendLabel*
                */
                String elseLabel = newLabel();
                String ifendLabel = newLabel();
                labelStack.push(ifendLabel);
                emit("breq " + ifCondExpr + ", 0, " + elseLabel);
                visit(ctx.getChild(3));
                emit("goto " + ifendLabel);
                emit(elseLabel + ":");
                visit(ctx.getChild(4));
                emit(ifendLabel + ":");
                labelStack.pop();
                break;
            case "while": // WHILE expr DO stat_seq ENDDO SEMI |
                /* 
                    *whileStartLabel*
                    condExpr = visit(ctx.getChild(1));
                    breq condExpr, 0, endLabel
                    visit(ctx.getChild(3));
                    goto whileStartLabel
                    *whileEndLabel*
                */
                String whileStartLabel = newLabel();
                String whileEndLabel = newLabel();
                labelStack.push(whileEndLabel);
                emit(whileStartLabel + ":");
                String whileCondExpr = visit(ctx.getChild(1)).split(" ")[0];
                emit("breq " + whileCondExpr + ", 0, " + whileEndLabel);
                visit(ctx.getChild(3));
                emit("goto " + whileStartLabel);
                emit(whileEndLabel + ":");
                labelStack.pop();
                break;
            case "for": // FOR ID ASSIGN expr TO expr DO stat_seq ENDDO SEMI
                /*
                    id = ctx.getChild(1).getText();
                    String forLoopStartExpr = visit(ctx.getChild(3)).split(" ")[0];
                    String forLoopEndExpr = visit(ctx.getChild(5)).split(" ")[0];
                    assign forLoopStartExpr to value of id
                    *startforloop*
                    brgt id.value, forLoopEndExpr, afterForLoop
                    visit(ctx.getChild(7));
                    add id.val, 1, id.val
                    goto startforloop
                    *afterForLoop*
                */
                String forStartLabel = newLabel();
                String forEndLabel = newLabel();

                String id = ctx.getChild(1).getText();
                String forLoopStartExpr = visit(ctx.getChild(3)).split(" ")[0];
                String forLoopEndExpr = visit(ctx.getChild(5)).split(" ")[0];
                emit("assign " + id + ", " + forLoopStartExpr);
                labelStack.push(forEndLabel);
                emit(forStartLabel + ":");
                emit("brgt " + id + ", " + forLoopEndExpr + ", " + forEndLabel);
                visit(ctx.getChild(7));
                emit("add " + id + ", " + "1, " + id);
                emit("goto " + forStartLabel);
                emit(forEndLabel + ":");
                labelStack.pop();
                break;
            case "break": // BREAK SEMI
            /*
                startwhilelabel
                while() {
                    break;

                }
                endwhilelabel
            */
                emit("goto " + this.labelStack.peek());
                break;
            case "return":
                String[] retVal = visit(ctx.getChild(1)).split(" ");
                emit("return " + retVal[0]);
                break;
            case "let":
                symTable.openScope();
                visit(ctx.getChild(1));
                visit(ctx.getChild(3));
                symTable.closeScope();
                break;
            default: // ID id_tail;
                String currId = ctx.getChild(0).getText();
                String idTail = visit(ctx.getChild(1));
                String[] params = idTail.split("#");
                boolean isAssign = ctx.getChild(1).getText().substring(0,2).equals(":=");
                boolean isArray = symTable.lookupSymbol(currId).isArray();

                if(params.length > 1) {
                    if(isAssign) {
                        String allParams = "";
                        for(int i=1; i<params.length; i++) {
                            String[] valType = params[i].split(" ");
                            allParams += ", " + valType[0];
                        }
                        emit("callr " + currId + ", " + params[0] + allParams);
                    } else {
                        String allParams = "";
                        for(int i=1; i<params.length; i++) {
                            String[] valType = params[i].split(" ");
                            allParams += ", " + valType[0];
                        }
                        String[] valType = params[0].split(" ");
                        emit("call " + currId + ", " + valType[0] + allParams);
                    }
                } else {
                    if(!isAssign && !isArray) {
                        System.out.println("Should be unreachable");
                    }
                    //Regular assign "F := 2"
                    else if(isAssign && !isArray && !params[0].contains(";")) {
                        String[] valType = params[0].split(" ");
                        emit("assign " + currId + ", " + valType[0]);
                    }
                    // Assigning indexed array val to non-array "F := C[2]"
                    else if(isAssign && !isArray && params[0].contains(";")) {
                        String[] arrayInfo = params[0].split(";");
                        emit("load " + currId + ", " + arrayInfo[0] + ", " + arrayInfo[1]);
                    }

                    // Assigning array to array "C := C"
                    else if(isAssign && isArray) {
                        String[] valType = params[0].split(" ");
                        emit("assign " + currId + ", " + valType[0]);
                    }
                    else {
                        // System.out.println("1");
                        String[] moreArrayInfo = params[0].split(";");
                        // Assigning val to indexed array "C[1] := 2"
                        if(!isAssign && moreArrayInfo.length == 2) {
                            // System.out.println("2");
                            String[] valType0 = moreArrayInfo[0].split(" ");
                            String[] valType1 = moreArrayInfo[1].split(" ");
                            emit("store " + currId + ", " + valType0[0] + ", " + valType1[0]);
                        }
                        // Assigning indexed array val to indexed array "C[1] := ZZ[2]"
                        // else if(!isAssign && moreArrayInfo.length == 3) {
                        //     System.out.println("3");
                        //     String tmp = newTemp();
                        //     emit("load, " + tmp + ", " + moreArrayInfo[1] + ", " + moreArrayInfo[2]);
                        //     emit("store, " + currId + ", " + moreArrayInfo[0] + ", " + tmp);
                        // } 
                        else {
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
            case 28: // assign
                return visit(ctx.getChild(1));
            case 9: // lbrack
                //String tmp = newTemp();
                //System.out.println(visit(ctx.getChild(1)))
                //emit("load, " + tmp + ", " + visit(ctx.getChild(1)) + ", " + visit(ctx.getChild(4)));
                return visit(ctx.getChild(1)) + ";" + visit(ctx.getChild(4));
                //return tmp;
            case 7: // lparen
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
            return ctx.getStart().getText() + "#" + params;
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
            String tmp = newTemp();
            String[] valType = lvalueTail.split(" ");
            emit("load " + tmp + ", " + id + ", " + valType[0]);
            this.symTable.addVariable(tmp, this.symTable.getType(id));
            return tmp + " " + this.symTable.getType(id);
        }
        
        return id + " " + this.symTable.getType(id);
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
        String[] andTermValAndType = visit(ctx.getChild(0)).split(" ");
        String andTermVal = andTermValAndType[0];
        String eTailStr = visit(ctx.getChild(1));

        if(eTailStr == null) {
            return andTermValAndType[0] + " " + andTermValAndType[1];
        }
        String[] eTailValAndType = eTailStr.split(" ");
        String eTailVal = eTailValAndType[0];

        String tmp = newTemp();
        emit("or " + andTermVal + ", " + eTailVal + ", " + tmp);
        this.symTable.addVariable(tmp, "int");

        return tmp + " int";
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

        String[] andTermValAndType = visit(ctx.getChild(1)).split(" ");
        String andTermVal = andTermValAndType[0];
        String eTailStr = visit(ctx.getChild(2));

        if(eTailStr == null) {
            return andTermValAndType[0] + " " + andTermValAndType[1];
        }
        String[] eTailValAndType = eTailStr.split(" ");
        String eTailVal = eTailValAndType[0];

        String tmp = newTemp();
        emit("or " + andTermVal + ", " + eTailVal + ", " + tmp);
        this.symTable.addVariable(tmp, "int");

        return tmp + " int";
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
        
        String[] compTermValAndType = visit(ctx.getChild(0)).split(" ");

        String compTermVal = compTermValAndType[0];
        String andTailStr= visit(ctx.getChild(1));
        if(andTailStr == null) {
            return compTermValAndType[0] + " " + compTermValAndType[1];
        }
        String[] andTailValAndType = andTailStr.split(" ");
        String andTailVal = andTailValAndType[0];

        String tmp = newTemp();
        emit("and " + compTermVal + ", " + andTailVal + ", " + tmp);

        this.symTable.addVariable(tmp, "int");

        return tmp + " int";
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

        String[] compTermValAndType = visit(ctx.getChild(1)).split(" ");
        String compTermVal = compTermValAndType[0];
        String andTailStr = visit(ctx.getChild(2));

        if(andTailStr == null) {
            return compTermValAndType[0] + " " + compTermValAndType[1];
        }
        String[] andTailValAndType = andTailStr.split(" ");
        String andTailVal = andTailValAndType[0];

        String tmp = newTemp();
        emit("and " + compTermVal + ", " + andTailVal + ", " + tmp);
        this.symTable.addVariable(tmp, "int");

        return tmp + " int";
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

        String[] divTermValAndType = visit(ctx.getChild(0)).split(" ");

        String divTermVal = divTermValAndType[0];
        String compTailStr = visit(ctx.getChild(1));
        if(compTailStr == null) {
            return divTermValAndType[0] + " " + divTermValAndType[1];
        }

        String[] compTailValAndType = compTailStr.split(" ");
        String compTailVal = compTailValAndType[0];

        String comparatorOp = ctx.getChild(1).getChild(0).getText();
        String lbl1 = newLabel();
        String lbl2 = newLabel();
        String tmp = newTemp();

        switch(comparatorOp) {
            case "!=":
                emit("breq " + divTermVal + ", " + compTailVal + ", " + lbl1);
                break;
            case "==":
                emit("brneq " + divTermVal + ", " + compTailVal + ", " + lbl1);
                break;
            case ">=":
                emit("brlt " + divTermVal + ", " + compTailVal + ", " + lbl1);
                break;
            case "<=":
                emit("brlt " + divTermVal + ", " + compTailVal + ", " + lbl1);
                break;
            case "<":
                emit("brgeq " + divTermVal + ", " + compTailVal + ", " + lbl1);
                break;
            case ">":
                emit("brleq " + divTermVal + ", " + compTailVal + ", " + lbl1);
                break;
        }
        emit("add 1, 0, " + tmp);
        emit("goto " + lbl2);
        emit(lbl1 + ":");
        emit("add 0, 0, " + tmp);
        emit(lbl2 + ":");

        this.symTable.addVariable(tmp, "int");

        return tmp + " int";
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

        String[] divTermValAndType = visit(ctx.getChild(1)).split(" ");
        String divTermVal = divTermValAndType[0];

        return divTermVal + " " + divTermValAndType[1];
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

        String[] multTermValAndType = visit(ctx.getChild(0)).split(" ");

        String multTermVal = multTermValAndType[0];
        String divTailStr= visit(ctx.getChild(1));
        if(divTailStr == null) {
            return multTermValAndType[0] + " " + multTermValAndType[1];
        }
        String[] divTailValAndType = divTailStr.split(" ");
        String divTailVal = divTailValAndType[0];

        String tmp = newTemp();
        String tmpType = "";
        if(multTermValAndType[1].equals(divTailValAndType[1])) {
            tmpType = multTermValAndType[1];
        } else if(multTermValAndType[1].equals("float") || divTailValAndType[1].equals("float")) {
            tmpType = "float";
        } else {
            tmpType = "int";
        }
        this.symTable.addVariable(tmp, tmpType);

        emit("div " + multTermVal + ", " + divTailVal + ", " + tmp);

        return tmp + " " + tmpType;
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

        String[] multTermValAndType = visit(ctx.getChild(1)).split(" ");
        String multTermVal = multTermValAndType[0];
        String divTailStr = visit(ctx.getChild(2));

        if(divTailStr == null) {
            return multTermValAndType[0] + " " + multTermValAndType[1];
        }
        String[] divTailValAndType = divTailStr.split(" ");
        String divTailVal = divTailValAndType[0];

        String tmp = newTemp();
        String tmpType = "";
        if(multTermValAndType[1].equals(divTailValAndType[1])) {
            tmpType = multTermValAndType[1];
        } else if(multTermValAndType[1].equals("float") || divTailValAndType[1].equals("float")) {
            tmpType = "float";
        } else {
            tmpType = "int";
        }
        this.symTable.addVariable(tmp, tmpType);

        emit("div " + multTermVal + ", " + divTailVal + ", " + tmp);

        return tmp + " " + tmpType;
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

        String[] subTermValAndType = visit(ctx.getChild(0)).split(" ");

        String subTermVal = subTermValAndType[0];
        String multTailStr= visit(ctx.getChild(1));
        if(multTailStr == null) {
            return subTermValAndType[0] + " " + subTermValAndType[1];
        }
        String[] multTailValAndType = multTailStr.split(" ");
        String multTailVal = multTailValAndType[0];

        String tmp = newTemp();
        String tmpType = "";
        if(subTermValAndType[1].equals(multTailValAndType[1])) {
            tmpType = subTermValAndType[1];
        } else if(subTermValAndType[1].equals("float") || multTailValAndType[1].equals("float")) {
            tmpType = "float";
        } else {
            tmpType = "int";
        }
        this.symTable.addVariable(tmp, tmpType);

        emit("mult " + subTermVal + ", " + multTailVal + ", " + tmp);

        return tmp + " " + tmpType;
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

        String[] subTermValAndType = visit(ctx.getChild(1)).split(" ");
        String subTermVal = subTermValAndType[0];
        String multTailStr = visit(ctx.getChild(2));

        if(multTailStr == null) {
            return subTermValAndType[0] + " " + subTermValAndType[1];
        }
        String[] multTailValAndType = multTailStr.split(" ");
        String multTailVal = multTailValAndType[0];

        String tmp = newTemp();
        String tmpType = "";
        if(subTermValAndType[1].equals(multTailValAndType[1])) {
            tmpType = subTermValAndType[1];
        } else if(subTermValAndType[1].equals("float") || multTailValAndType[1].equals("float")) {
            tmpType = "float";
        } else {
            tmpType = "int";
        }
        this.symTable.addVariable(tmp, tmpType);


        emit("mult " + subTermVal + ", " + multTailVal + ", " + tmp);
        return tmp + " " + tmpType;
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

        String[] addTermValAndType = visit(ctx.getChild(0)).split(" ");

        String addTermVal = addTermValAndType[0];
        String subTailStr= visit(ctx.getChild(1));
        if(subTailStr == null) {
            return addTermValAndType[0] + " " + addTermValAndType[1];
        }
        String[] subTailValAndType = subTailStr.split(" ");
        String subTailVal = subTailValAndType[0];

        String tmp = newTemp();
        String tmpType = "";
        if(addTermValAndType[1].equals(subTailValAndType[1])) {
            tmpType = addTermValAndType[1];
        } else if(addTermValAndType[1].equals("float") || subTailValAndType[1].equals("float")) {
            tmpType = "float";
        } else {
            tmpType = "int";
        }
        this.symTable.addVariable(tmp, tmpType);
        emit("sub " + addTermVal + ", " + subTailVal + ", " + tmp);

        return tmp + " " + tmpType;
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

        String[] addTermValAndType = visit(ctx.getChild(1)).split(" ");
        String addTermVal = addTermValAndType[0];
        String subTailStr = visit(ctx.getChild(2));

        if(subTailStr == null) {
            return addTermValAndType[0] + " " + addTermValAndType[1];
        }
        String[] subTailValAndType = subTailStr.split(" ");
        String subTailVal = subTailValAndType[0];

        String tmp = newTemp();
        String tmpType = "";
        if(addTermValAndType[1].equals(subTailValAndType[1])) {
            tmpType = addTermValAndType[1];
        } else if(addTermValAndType[1].equals("float") || subTailValAndType[1].equals("float")) {
            tmpType = "float";
        } else {
            tmpType = "int";
        }
        this.symTable.addVariable(tmp, tmpType);

        emit("sub " + addTermVal + ", " + subTailVal + ", " + tmp);

        return tmp + " " + tmpType;
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

        String[] powTermValAndType = visit(ctx.getChild(0)).split(" ");

        String powTermVal = powTermValAndType[0];
        String addTailStr= visit(ctx.getChild(1));
        if(addTailStr == null) {
            return powTermValAndType[0] + " " + powTermValAndType[1];
        }
        String[] addTailValAndType = addTailStr.split(" ");
        String addTailVal = addTailValAndType[0];

        String tmp = newTemp();
        String tmpType = "";
        
        if(powTermValAndType[1].equals(addTailValAndType[1])) {
            tmpType = powTermValAndType[1];
        } else if(powTermValAndType[1].equals("float") || addTailValAndType[1].equals("float")) {
            tmpType = "float";
        } else {
            tmpType = "int";
        }
        this.symTable.addVariable(tmp, tmpType);
        emit("add " + powTermVal + ", " + addTailVal + ", " + tmp);

        return tmp + " " + tmpType;
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

        String[] powTermValAndType = visit(ctx.getChild(1)).split(" ");
        String powTermVal = powTermValAndType[0];
        String addTailStr = visit(ctx.getChild(2));
        
        if(addTailStr == null) {
            return powTermValAndType[0] + " " + powTermValAndType[1];
        }

        String[] addTailValAndType = addTailStr.split(" ");
        String addTailVal = addTailValAndType[0];
        
        String tmp = newTemp();
        String tmpType = "";
        if(powTermValAndType[1].equals(addTailValAndType[1])) {
            tmpType = powTermValAndType[1];
        } else if(powTermValAndType[1].equals("float") || addTailValAndType[1].equals("float")) {
            tmpType = "float";
        } else {
            tmpType = "int";
        }
        this.symTable.addVariable(tmp, tmpType);
        emit("add " + powTermVal + ", " + addTailVal + ", " + tmp);

        return tmp + " " + tmpType;
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

        String[] factorValAndType = visit(ctx.getChild(0)).split(" ");

        String factorVal = factorValAndType[0];
        String powTailStr = visit(ctx.getChild(1));
        if(powTailStr == null) {
            return factorValAndType[0] + " " + factorValAndType[1];
        }
        String[] powTailValAndType = powTailStr.split(" ");
        String powTailVal = powTailValAndType[0];

        String tmp = newTemp();
        // emit("add, 1, 0, " + tmp);
        // for(int i=1; i<= Integer.parseInt(powTailVal); i++) {
        //     emit("mult " + tmp + ", " + factorVal + ", " + tmp);
        // }
        this.symTable.addVariable(tmp, factorValAndType[1]);
        emit("pow " + factorVal + ", " + powTailVal + ", " + tmp);
        return tmp + " " + factorValAndType[1];
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

        String[] factorValAndType = visit(ctx.getChild(1)).split(" ");
        String factorVal = factorValAndType[0];
        String powTailStr = visit(ctx.getChild(2));
        if(powTailStr == null) {
            return factorValAndType[0] + " " + factorValAndType[1];
        }

        String[] powTailValAndType = powTailStr.split(" ");
        String powTailVal = powTailValAndType[0];

        String tmp = newTemp();
        this.symTable.addVariable(tmp, "int");
        emit("pow " + factorVal + ", " + powTailVal + ", " + tmp);

        return tmp + " int";
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
            case 7: // LPAREN
                return visit(ctx.getChild(1));
            case 52: // lvalue / ID
                return visit(ctx.getChild(0));
            case 53: // constant / INTLIT
                return ctx.getChild(0).getText() + " int";
            case 54: // constant / FLOATLIT
                return ctx.getChild(0).getText() + " float";
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
        return expr + "#" + exprList;
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