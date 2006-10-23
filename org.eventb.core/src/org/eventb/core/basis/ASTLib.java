/**
 * 
 */
package org.eventb.core.basis;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

/**
 * Class containing frequently used static helper functions pertaining to the
 * event-B AST
 * 
 * @author Farhad Mehta
 *
 */
public class ASTLib {

	private static FormulaFactory formulaFactory = FormulaFactory.getDefault();
	
	private ASTLib() {
		// Class not intended to be instantiated
	}
	
	public static Predicate parsePredicate(String str){
		IParseResult plr = formulaFactory.parsePredicate(str);
		if (plr.isSuccess()) return plr.getParsedPredicate();
		return null;
	}
	
	public static Expression parseExpression(String str){
		IParseResult plr = formulaFactory.parseExpression(str);
		if (plr.isSuccess()) return plr.getParsedExpression();
		else return null;
	}
	
	public static Type parseType(String str){
		IParseResult plr = formulaFactory.parseType(str);
		if (plr.isSuccess()) return plr.getParsedType();
		else return null;
	}
	
	public static boolean typeCheckClosed(Formula f, ITypeEnvironment t) {
		ITypeCheckResult tcr = f.typeCheck(t);
		// new free variables introduced
		if (tcr.isSuccess()) {
			return tcr.getInferredEnvironment().isEmpty();
		}
		return false;
	}
	
	public static ITypeEnvironment makeTypeEnvironment() {
		return formulaFactory.makeTypeEnvironment();
	}

}
