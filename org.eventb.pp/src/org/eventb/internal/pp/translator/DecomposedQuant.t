/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.translator;

import java.util.*;
import java.math.BigInteger;

import org.eventb.core.ast.*;


/**
 * ...
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings("unused")
public class DecomposedQuant {

%include {Formula.tom}

	protected final LinkedList<BoundIdentDecl> identDecls = new LinkedList<BoundIdentDecl>();
	private boolean hasPushed = false;
	protected final FormulaFactory ff;

	public DecomposedQuant(FormulaFactory ff) {
		this.ff = ff;
	}
	
	public DecomposedQuant(FormulaFactory ff, BoundIdentDecl[] ids) {
		this(ff);
		for(BoundIdentDecl decl: ids) {
			assert !(decl.getType() instanceof ProductType) : "Only decomposed identifiers allowed!";
			identDecls.add(decl);
		}
		hasPushed = true;
	}
	
	public Expression addQuantifier(Type type, SourceLocation loc) {
		return addQuantifier(type, "x", loc);
	}	

	public Expression addQuantifier(Type type, String name, SourceLocation loc) {
		assert !hasPushed : "Tried to add quantifiers after having started pushing stuff";
		return mapletOfType(type, name, loc);
	}
	
	public Expression push(Expression expr) {
		hasPushed = true;
		return expr.shiftBoundIdentifiers(offset(), ff); 
	}
	
	public static Expression pushThroughAll(
		Expression expr, FormulaFactory ff, DecomposedQuant... quantifications) {
		int totalOffset = 0;
		for(DecomposedQuant quantification: quantifications) {
			quantification.hasPushed = true;
			totalOffset += quantification.offset();
		}
		return expr.shiftBoundIdentifiers(totalOffset, ff);
	}
	
	public Expression makeQuantifiedExpression(
		int tag, Predicate pred, Expression expr, SourceLocation loc) {

		return ff.makeQuantifiedExpression(
			tag, identDecls, pred, expr, loc, QuantifiedExpression.Form.Explicit);
	}
		
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
			_ -> {
				final int index = identDecls.size();
				identDecls.add(0, ff.makeBoundIdentDecl(name, loc, type));
				return ff.makeBoundIdentifier(index, loc, type);	
			}
		}
	}
}