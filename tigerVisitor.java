// Generated from tiger.g4 by ANTLR 4.7.1
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link tigerParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface tigerVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link tigerParser#tiger_program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTiger_program(tigerParser.Tiger_programContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#declaration_segment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration_segment(tigerParser.Declaration_segmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#type_declaration_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_declaration_list(tigerParser.Type_declaration_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#var_declaration_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_declaration_list(tigerParser.Var_declaration_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#function_declaration_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_declaration_list(tigerParser.Function_declaration_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#type_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_declaration(tigerParser.Type_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(tigerParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#type_id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_id(tigerParser.Type_idContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#var_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_declaration(tigerParser.Var_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#id_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId_list(tigerParser.Id_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#id_list_tail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId_list_tail(tigerParser.Id_list_tailContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#optional_init}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOptional_init(tigerParser.Optional_initContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#function_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_declaration(tigerParser.Function_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#param_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_list(tigerParser.Param_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#param_list_tail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_list_tail(tigerParser.Param_list_tailContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#ret_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRet_type(tigerParser.Ret_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(tigerParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#stat_seq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_seq(tigerParser.Stat_seqContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#stat_seq_tail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_seq_tail(tigerParser.Stat_seq_tailContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat(tigerParser.StatContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#id_tail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId_tail(tigerParser.Id_tailContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#assign_tail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssign_tail(tigerParser.Assign_tailContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLvalue(tigerParser.LvalueContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#lvalue_tail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLvalue_tail(tigerParser.Lvalue_tailContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#else_stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElse_stat(tigerParser.Else_statContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(tigerParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#e_tail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitE_tail(tigerParser.E_tailContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#and_term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnd_term(tigerParser.And_termContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#and_tail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnd_tail(tigerParser.And_tailContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#comparison_term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_term(tigerParser.Comparison_termContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#comparison_tail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_tail(tigerParser.Comparison_tailContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#div_term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDiv_term(tigerParser.Div_termContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#div_tail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDiv_tail(tigerParser.Div_tailContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#mult_term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMult_term(tigerParser.Mult_termContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#mult_tail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMult_tail(tigerParser.Mult_tailContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#sub_term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSub_term(tigerParser.Sub_termContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#sub_tail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSub_tail(tigerParser.Sub_tailContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#add_term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdd_term(tigerParser.Add_termContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#add_tail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdd_tail(tigerParser.Add_tailContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#pow_term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPow_term(tigerParser.Pow_termContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#pow_tail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPow_tail(tigerParser.Pow_tailContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactor(tigerParser.FactorContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant(tigerParser.ConstantContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#expr_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr_list(tigerParser.Expr_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link tigerParser#expr_list_tail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr_list_tail(tigerParser.Expr_list_tailContext ctx);
}