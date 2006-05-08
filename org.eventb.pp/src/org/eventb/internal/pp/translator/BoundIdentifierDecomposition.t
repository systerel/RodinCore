/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.translator;

import java.math.BigInteger;
import java.util.*;

import org.eventb.core.ast.*;


/**
 * Implements the Bound Identifier Decomposition.
 * The method decomposeBoundIdentifiers decomposes all bound identifiers of a predicate,
 * such that after the decomposition there are no bound identifiers of cartesian product type.
 * Example:		∀x·x=1↦2 ⇒ x∈S	becomes: ∀x0,x1·x0↦x1=1↦2 ⇒ x0↦x1∈S
 * 				
 * @author Matthias Konrad
 */
@SuppressWarnings("unused")
public class BoundIdentifierDecomposition extends IdentityTranslator {

	private static class Substitute {
		final Expression expr;
		final int boundIndex;
		
		Substitute(Expression expr, int boundIndex) {
			this.expr = expr;
			this.boundIndex = boundIndex;
		}

	}

	%include {Formula.tom}
	
	private final List<Substitute> mapletOffsets;
	private int count;
	
	private BoundIdentifierDecomposition(FormulaFactory ff) {
		super(ff);
		mapletOffsets = new LinkedList<Substitute>();
		count = 0;
	}
	
	private BoundIdentifierDecomposition(FormulaFactory ff, List<Substitute> mapletOffsets, int count) {
		super(ff);
		this.mapletOffsets = new LinkedList<Substitute>(mapletOffsets);
		this.count = count;
	}
	
	/**
	 * Implements the Bound Identifier Decomposition
 	 * Example:	∀x·x=1↦2 ⇒ x∈S	becomes: ∀x0,x1·x0↦x1=1↦2 ⇒ x0↦x1∈S	
 	 * @param pred the predicate that is decomposed
	 * @param ff the Formula Factory used during the decomposition
	 * @return a new predicate, which is the decomposed version of pred
	 */
	public static Predicate decomposeBoundIdentifiers(Predicate pred, FormulaFactory ff) {
		pred = new BoundIdentifierDecomposition(ff).translate(pred);
		
		return pred;
	}
	
	@Override
	protected Expression translate(Expression expr){
		SourceLocation loc = expr.getSourceLocation();		
		%match (Expression expr) {
			Cset(is, P, E) | Qunion(is, P, E) | Qinter(is, P, E) -> {
				BoundIdentifierDecomposition ic =
						new BoundIdentifierDecomposition(ff, mapletOffsets, count);
				DecomposedQuant quant = new DecomposedQuant(ff);
				Collections.reverse(Arrays.asList(`is));
				List<Expression> quantifiers = new LinkedList<Expression>();
				for (BoundIdentDecl decl: `is) {
					quantifiers.add(0,  
						quant.addQuantifier(decl.getType(), decl.getName(), decl.getSourceLocation()));
				}
				for (Expression quantifier: quantifiers) {
					ic.mapletOffsets.add(0, 
						new Substitute(quantifier, ic.count + quant.offset()));
				} 	
				ic.count += quant.offset();
				return quant.makeQuantifiedExpression(
					expr.getTag(),
					ic.translate(`P),
					ic.translate(`E),
					loc);
			}
			BoundIdentifier(idx) -> {
				Substitute p = mapletOffsets.get(`idx);
				return p.expr.shiftBoundIdentifiers(count - p.boundIndex, ff);
			}
			_ -> {
				return super.translate(expr);
			}
		}
	}
	
	@Override
	protected Predicate translate(Predicate pred) {
		SourceLocation loc = pred.getSourceLocation();		
		%match (Predicate pred) {
			ForAll(is, P) | Exists(is, P) -> {
				BoundIdentifierDecomposition ic = 
						new BoundIdentifierDecomposition(ff, mapletOffsets, count);
				DecomposedQuant quant = new DecomposedQuant(ff);
				Collections.reverse(Arrays.asList(`is));
				List<Expression> quantifiers = new LinkedList<Expression>();
				for (BoundIdentDecl decl: `is) {
					quantifiers.add(0,  
						quant.addQuantifier(decl.getType(), decl.getName(), decl.getSourceLocation()));
				}
				for (Expression quantifier: quantifiers) {
					ic.mapletOffsets.add(0,
						new Substitute(quantifier, ic.count + quant.offset()));
				} 	
				ic.count += quant.offset();
				return quant.makeQuantifiedPredicate(
					pred.getTag(),
					ic.translate(`P),
					loc);
			}
			_ -> {
				return super.translate(pred);
			}
		}
	}
}
