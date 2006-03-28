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
 * Implements the Identifier Decomposition.
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings("unused")
public class IdentifierDecomposition extends IdentityTranslator {

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
	
	List<Pair<Type, Integer>> identTypes;
	Counter c;
	
	public IdentifierDecomposition() {
		identTypes = new LinkedList<Pair<Type, Integer>>();
		c = new Counter();
	}
	
	public IdentifierDecomposition(List<Pair<Type, Integer>> identTypes, Counter c) {
		this.identTypes = new LinkedList<Pair<Type, Integer>>(identTypes);
		this.c = new Counter(c);
	}
	
	public Expression translate(Expression expr, FormulaFactory ff){
		Translator.QuantMapletBuilder mb = new Translator.QuantMapletBuilder();
		SourceLocation loc = expr.getSourceLocation();		
		%match (Expression expr) {
			Cset(is, P, E) | Qunion(is, P, E) | Qinter(is, P, E) -> {
				List<BoundIdentDecl> identDecls = new LinkedList<BoundIdentDecl>();
				for (BoundIdentDecl decl: `is) {
					mb.calculate(decl.getType(), 0, decl.getSourceLocation(), ff);
					c.add(mb.getIdentDecls().size());
					identTypes.add (0, 
						new Pair<Type, Integer>(decl.getType(), new Integer(c.value())));
					identDecls.addAll(mb.getIdentDecls());					
				}	
				return ff.makeQuantifiedExpression(
					expr.getTag(),
					identDecls,
					new IdentifierDecomposition(identTypes, c).translate(`P, ff),
					new IdentifierDecomposition(identTypes, c).translate(`E, ff),
					loc,
					QuantifiedExpression.Form.Explicit);
			}
			BoundIdentifier(idx) -> {
				Pair<Type, Integer> p = identTypes.get(`idx);
				mb.calculate(p.first(), c.value() - p.second().intValue(), loc, ff);
				return mb.getMaplet();
			}
			_ -> {
				return super.translate(expr, ff);
			}
		}
	}
	
	public Predicate translate(Predicate P, FormulaFactory ff) {
		%match (Predicate P) {
			ForAll(is, P) | Exists(is, P) -> {
				throw new AssertionError("not yet supported: " + P);
			}
			_ -> {
				return super.translate(P, ff);
			}
		}
	}
}
