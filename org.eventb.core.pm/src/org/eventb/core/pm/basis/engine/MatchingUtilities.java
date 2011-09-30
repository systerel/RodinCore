/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.basis.engine;

import static org.eventb.core.ast.LanguageVersion.V2;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.plugin.PMPlugin;

/**
 * 
 * @author maamria
 *
 */
public class MatchingUtilities {

	/**
	 * <p>
	 * Checks whether two objects are of the same class.
	 * </p>
	 * 
	 * @param o1 the first object
	 * @param o2 the second object
	 * @return whether the two objects are of the same class
	 */
	public static boolean sameClass(Object o1, Object o2) {
		return o1.getClass().equals(o2.getClass());
	}
	
	/**
	 * Literal predicate true.
	 */
	public static final Predicate BTRUE = FormulaFactory.getDefault()
			.makeLiteralPredicate(Formula.BTRUE, null);

	/**
	 * Make sure tag is for an associative expression.
	 * <p>
	 * This method checks whether the operator is AC.
	 * 
	 * @param tag
	 * @return
	 */
	public static boolean isAssociativeCommutative(int tag) {
		if (tag == AssociativeExpression.BCOMP
				|| tag == AssociativeExpression.FCOMP) {
			return false;
		}
		return true;
	}
	
	/**
	 * Logs the given exception with the message.
	 * @param exc the exception
	 * @param message the message
	 */
	public static void log(Throwable exc, String message) {
		if (message == null) {
			message = "Unknown context"; //$NON-NLS-1$
		}
		IStatus status = new Status(IStatus.ERROR, PMPlugin.PLUGIN_ID,
				IStatus.ERROR, message, exc);
		PMPlugin.getDefault().getLog().log(status);
	}
	
	/**
	 * <p>
	 * Utility method to parse a string as a formula knowing beforehand whether
	 * it is a an expression or predicate.
	 * </p>
	 * <p>
	 * Use only for theory formulas.
	 * </p>
	 * 
	 * @param formStr
	 *            the formula string
	 * @param isExpression
	 *            whether to parse an expression or a predicate
	 * @return the parsed formula or <code>null</code> if there was an error
	 */
	public static Formula<?> parseFormula(String formStr,boolean isExpression, FormulaFactory factory) {
		Formula<?> form = null;
		if (isExpression) {
			IParseResult r = factory.parseExpressionPattern(formStr, V2, null);
			form = r.getParsedExpression();
		} else {
			IParseResult r = factory.parsePredicatePattern(formStr, V2, null);
			form = r.getParsedPredicate();
		}
		return form;
	}
	
	/**
	 * Returns the associative (potentially extended) expression that fit the given details.
	 * @param tag the tag
	 * @param factory the formula factory
	 * @param exps the expressions
	 * @return the resultant expression
	 */
	public static Expression makeAppropriateAssociativeExpression(int tag, FormulaFactory factory, Expression... exps){
		List<Expression> es = new ArrayList<Expression>();
		for (Expression e : exps) {
			if (e != null) {
				es.add(e);
			}
		}
		if(es.size() < 1){
			throw 
			 new IllegalArgumentException("Cannot make associative expression from empty array of children.");
		}
		if (es.size() == 1)
			return es.get(0);
		IFormulaExtension extension = factory.getExtension(tag);
		if(extension!=null){
			return factory.makeExtendedExpression(
					(IExpressionExtension)extension, 
					es.toArray(new Expression[es.size()]), 
					new Predicate[0], null);
		}
		else {
			return factory.makeAssociativeExpression(tag,
					 es.toArray(new Expression[es.size()]), null);
		}
	}
	
	/**
	 * Returns an array of the non-<code>null</code> expressions occuring in the given array in the same order.
	 * @param exps the expressions
	 * @return the array of non-<code>null</code> expression
	 */
	public static Expression[] getExpressionArray(Expression... exps){
		List<Expression> list = new ArrayList<Expression>();
		for (Expression exp : exps){
			if (exp != null){
				list.add(exp);
			}
		}
		return list.toArray(new Expression[list.size()]);
	}

	/**
	 * Returns the associative predicate that fit the given details.
	 * @param tag the tag
	 * @param factory the formula factory
	 * @param preds the predicates
	 * @return the resultant predicate
	 */
	public static Predicate makeAssociativePredicate(int tag,
			FormulaFactory factory, Predicate... preds) {
		List<Predicate> es = new ArrayList<Predicate>();
		for (Predicate e : preds) {
			if (e != null) {
				es.add(e);
			}
		}
		if(es.size() < 1){
			throw 
			 new IllegalArgumentException("Cannot make associative predicate from empty array of children.");
		}
		if (es.size() == 1)
			return es.get(0);
		else {
			return factory.makeAssociativePredicate(tag,
					es.toArray(new Predicate[es.size()]), null);
		}
	}
	
	/**
	 * 
	 * @param <E> the type of the objects
	 * @param es the elements
	 * @return the list of non-null elements
	 */
	public static <E> List<E> getListWithoutNulls(E... es){
		List<E> list = new ArrayList<E>();
		for (E e : es){
			if (e != null){
				list.add(e);
			}
		}
		return list;
	}
	
	/**
	 * Returns whether the two arrays of declarations match (simple implementation).
	 * @param formulaDecs the formula declarations
	 * @param patternDecs the pattern declarations
	 * @param existingBinding the existing binding
	 * @return whether the declarations match
	 */
	public static boolean boundIdentDecsMatch(BoundIdentDecl[] formulaDecs, 
			BoundIdentDecl[] patternDecs, IBinding existingBinding){
		if(formulaDecs.length == patternDecs.length){
			int index = 0;
			for(BoundIdentDecl pDec: patternDecs){
				BoundIdentDecl fDec = formulaDecs[index];
				if(!existingBinding.canUnifyTypes(fDec.getType(), pDec.getType())){
					return false;
				}
				index++;
			}
			return true;
		}
		else 
			return false;
	}

}
