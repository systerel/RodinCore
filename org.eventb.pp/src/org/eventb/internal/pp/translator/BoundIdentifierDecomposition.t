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
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings("unused")
public class BoundIdentifierDecomposition extends IdentityTranslator {

	public static class Pair<T1, T2> {
		private T1 first;
		private T2 second;
		public Pair(T1 first, T2 second) {
			this.first = first;
			this.second = second;
		}
		
		T1 first() { return first; }
		T2 second() { return second; }
	}

	%include {Formula.tom}
	
	List<Pair<Expression, Integer>> mapletOffsets;
	Counter c;
	
	private BoundIdentifierDecomposition() {
		mapletOffsets = new LinkedList<Pair<Expression, Integer>>();
		c = new Counter();
	}
	
	private BoundIdentifierDecomposition(List<Pair<Expression, Integer>> mapletOffsets, Counter c) {
		this.mapletOffsets = new LinkedList<Pair<Expression, Integer>>(mapletOffsets);
		this.c = new Counter(c);
	}
	
	public static Predicate decomposeBoundIdentifiers(Predicate pred, FormulaFactory ff) {
		pred = new BoundIdentifierDecomposition().translate(pred, ff);
		
		return pred;
	}
	
	@Override
	protected Expression translate(Expression expr, FormulaFactory ff){
		SourceLocation loc = expr.getSourceLocation();		
		%match (Expression expr) {
			Cset(is, P, E) | Qunion(is, P, E) | Qinter(is, P, E) -> {
				BoundIdentifierDecomposition ic = new BoundIdentifierDecomposition(mapletOffsets, c);
				DecomposedQuant quant = new DecomposedQuant(ff);
				Collections.reverse(Arrays.asList(`is));
				List<Expression> quantifiers = new LinkedList<Expression>();
				for (BoundIdentDecl decl: `is) {
					quantifiers.add(0,  
						quant.addQuantifier(decl.getType(), decl.getName(), decl.getSourceLocation()));
				}
				for (Expression quantifier: quantifiers) {
					ic.mapletOffsets.add(0, 
						new Pair<Expression, Integer>(
							quantifier,
							new Integer(ic.c.value() + quant.offset())));
				} 	
				ic.c.add(quant.offset());
				return quant.makeQuantifiedExpression(
					expr.getTag(),
					ic.translate(`P, ff),
					ic.translate(`E, ff),
					loc);
			}
			BoundIdentifier(idx) -> {
				Pair<Expression, Integer> p = mapletOffsets.get(`idx);
				return p.first().shiftBoundIdentifiers(c.value() - p.second().intValue(), ff);
			}
			_ -> {
				return super.translate(expr, ff);
			}
		}
	}
	
	@Override
	protected Predicate translate(Predicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();		
		%match (Predicate pred) {
			ForAll(is, P) | Exists(is, P) -> {
				BoundIdentifierDecomposition ic = new BoundIdentifierDecomposition(mapletOffsets, c);
				DecomposedQuant quant = new DecomposedQuant(ff);
				Collections.reverse(Arrays.asList(`is));
				List<Expression> quantifiers = new LinkedList<Expression>();
				for (BoundIdentDecl decl: `is) {
					quantifiers.add(0,  
						quant.addQuantifier(decl.getType(), decl.getName(), decl.getSourceLocation()));
				}
				for (Expression quantifier: quantifiers) {
					ic.mapletOffsets.add(0,
						new Pair<Expression, Integer>(
							quantifier,
							new Integer(ic.c.value() + quant.offset())));
				} 	
				ic.c.add(quant.offset());
				return quant.makeQuantifiedPredicate(
					pred.getTag(),
					ic.translate(`P, ff),
					loc);
			}
			_ -> {
				return super.translate(pred, ff);
			}
		}
	}
}
