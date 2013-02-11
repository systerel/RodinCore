/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pptrans.translator;

import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;


/**
 * Implements the decomposed quantification. The class is normaly used by
 * first calling the addQuantifier Method one ore more times. Followed by calls
 * to push. At the end a quantification is generated with a call to method 
 * makeQuantifiedExpression or makeQuantifiedPredicate.
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings({"unused", "cast"})
public class DecomposedQuant {

	%include {Type.tom}

	protected final LinkedList<BoundIdentDecl> identDecls = new LinkedList<BoundIdentDecl>();
	private boolean hasPushed = false;
	protected final FormulaFactory ff;

	/**
	 * @param ff the formula factory to be used
	 */
	public DecomposedQuant(FormulaFactory ff) {
		this.ff = ff;
	}
	
	/**
	 * This constructor is used, when a given quantification is modified.
	 * @param ff the formula factory to be used
	 * @param ids the bound identifiers of the source quantification
	 */
	public DecomposedQuant(FormulaFactory ff, BoundIdentDecl[] ids) {
		this(ff);
		for(BoundIdentDecl decl: ids) {
			assert !(decl.getType() instanceof ProductType) : "Only decomposed identifiers allowed!";
			identDecls.add(decl);
		}
		hasPushed = true;
	}
	
	/**
	 * Adds one or several instances of BoundIdentDecl, depending on type.
	 *  If type is CPROD(INT, INT), two bound identifiers would be added. 
	 * @param type type to be decomposed and added to the quantification
	 * @param loc source location for the new expression
	 * @return a new Identifier or maplet of Identifiers that refers to the BoundIdentDecl instances.
	 */
	public Expression addQuantifier(Type type, SourceLocation loc) {
		return addQuantifier(type, "x", loc);
	}	

	/**
	 * Adds one or several instances of BoundIdentDecl, depending on type.
	 * If type is CPROD(INT, INT), two bound identifiers would be added. 
	 * @param type type to be decomposed and added to the quantification
	 * @param name name for the new BoundIdentDecl instances
	 * @param loc source location for the new expression
	 * @return a new Identifier or maplet of Identifiers that refers to the BoundIdentDecl instances.
	 */
	public Expression addQuantifier(Type type, String name, SourceLocation loc) {
		assert !hasPushed : "Tried to add quantifiers after having started pushing stuff";
		return mapletOfType(type, name, loc);
	}
	
	/**
	 * If an expression that was previously outside of a quantification is no inside,
	 * its externally bound identifiers have to be shifted. This can be done safely by
	 * a call to push. As soon as the first expression is pushed, no more calls to
	 * addQuantifier are allowed! 
	 * @param expr the expression to be pushed
	 * @return the expression with shifted bound identifiers
	 */
	public Expression push(Expression expr) {
		hasPushed = true;
		return expr.shiftBoundIdentifiers(offset());
	}
	
	/**
	 * Exists for performance reasons. When an existing expression is placed inside two
	 * or more nested quantifications, pushThroughAll does all the necessary work with
	 * just one call to shiftBoundIdentifiers.
	 * @param expr the expression to be pushed
	 * @param ff the formula factory to be used
	 * @param quantifications the quantifications through which the expr needs to be pushed.
	 * @return the expression with shifted bound identifiers
	 */
	public static Expression pushThroughAll(
		Expression expr, FormulaFactory ff, DecomposedQuant... quantifications) {
		int totalOffset = 0;
		for(DecomposedQuant quantification: quantifications) {
			quantification.hasPushed = true;
			totalOffset += quantification.offset();
		}
		return expr.shiftBoundIdentifiers(totalOffset);
	}
	
	/**
	 * Generates a new quantified expression
	 * @param tag either Formula.EXISTS or Formula.FORALL
	 * @param pred the predicate to be used in the quantified expression
	 * @param expr the quantified expression
	 * @param loc the source location
	 * @return a new quantified expression
	 */
	public Expression makeQuantifiedExpression(
		int tag, Predicate pred, Expression expr, SourceLocation loc) {

		return ff.makeQuantifiedExpression(
			tag, identDecls, pred, expr, loc, QuantifiedExpression.Form.Explicit);
	}
		
	/**
	 * Generates a new quantified predicate
	 * @param tag either Formula.EXISTS or Formula.FORALL
	 * @param pred the quantified predicate
	 * @param loc the source location
	 * @return a new quantified predicate
	 */
	public Predicate makeQuantifiedPredicate(
		int tag, Predicate pred, SourceLocation loc) {

		return ff.makeQuantifiedPredicate(
			tag, identDecls, pred, loc);
	}

	protected List<BoundIdentDecl> getIdentDecls() {
		return identDecls;
	}
	
	public int offset() {
		return identDecls.size();
	}
	
	private Expression mapletOfType(Type type, String name, SourceLocation loc) {
		%match (Type type) {
			CProd (left, right) -> {
				// Process right child first
				final Expression r = mapletOfType(`right, name, loc);
				final Expression l = mapletOfType(`left, name, loc);
				return ff.makeBinaryExpression(Formula.MAPSTO, l, r, loc);
			}
		}
		final int index = identDecls.size();
		identDecls.add(0, ff.makeBoundIdentDecl(name, loc, type));
		return ff.makeBoundIdentifier(index, loc, type);	
	}
}
