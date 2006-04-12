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
	
	private IdentifierDecomposition() {
		identTypes = new LinkedList<Pair<Type, Integer>>();
		c = new Counter();
	}
	
	private IdentifierDecomposition(List<Pair<Type, Integer>> identTypes, Counter c) {
		this.identTypes = new LinkedList<Pair<Type, Integer>>(identTypes);
		this.c = new Counter(c);
	}
	
	public static Predicate decomposeIdentifiers(Predicate pred, FormulaFactory ff) {
		QuantMapletBuilder mb = new QuantMapletBuilder();

		//First the bound identifiers are decomposed
		pred = new IdentifierDecomposition().translate(pred, ff);
		
		//Then the free Identifiers are decomposed by introducing bound identifiers.
		if(pred.getFreeIdentifiers().length > 0) {
			Map<FreeIdentifier, Expression> identMap = new HashMap();
			Set<String> names = new HashSet<String>();
			LinkedList<BoundIdentDecl> identDecls = new LinkedList<BoundIdentDecl>();
			LinkedList<Predicate> bindings = new LinkedList<Predicate>();
			
			int c = 0;
			for(FreeIdentifier ident: pred.getFreeIdentifiers()) {
				if(!names.contains(ident.getName())) {
					names.add(ident.getName());
					mb.calculate(ident.getType(), c, null, ff);
					if(mb.getIdentDecls().size() > 1) {
						c = c + mb.getIdentDecls().size();
						for(BoundIdentDecl identDecl: mb.getIdentDecls()) {
							identDecls.addFirst(identDecl);
						}
						identMap.put(ident, mb.getMaplet());
						bindings.add(
							ff.makeRelationalPredicate(Formula.EQUAL, ident, mb.getMaplet(),null));	
					}
				}
			}
			if(identMap.size() > 0) {
				pred = pred.substituteFreeIdents(identMap, ff);
				
				pred = ff.makeQuantifiedPredicate(
					Formula.FORALL,
					identDecls,
					ff.makeBinaryPredicate(
							Formula.LIMP,
							bindings.size() > 1 ? 
								ff.makeAssociativePredicate(Formula.LAND, bindings,	null) :
								bindings.getFirst(),
							pred,
							null),
					null);
			}
		}
		return pred;
	}
	
	protected Expression translate(Expression expr, FormulaFactory ff){
		QuantMapletBuilder mb = new QuantMapletBuilder();
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
	
	protected Predicate translate(Predicate pred, FormulaFactory ff) {
		QuantMapletBuilder mb = new QuantMapletBuilder();
		SourceLocation loc = pred.getSourceLocation();		
		%match (Predicate pred) {
			ForAll(is, P) | Exists(is, P) -> {
				List<BoundIdentDecl> identDecls = new LinkedList<BoundIdentDecl>();
				for (BoundIdentDecl decl: `is) {
					mb.calculate(decl.getType(), 0, decl.getName(), decl.getSourceLocation(), ff);
					c.add(mb.getIdentDecls().size());
					identTypes.add (0, 
						new Pair<Type, Integer>(decl.getType(), new Integer(c.value())));
					identDecls.addAll(mb.getIdentDecls());					
				}	
				return ff.makeQuantifiedPredicate(
					pred.getTag(),
					identDecls,
					new IdentifierDecomposition(identTypes, c).translate(`P, ff),
					loc);
			}
			_ -> {
				return super.translate(pred, ff);
			}
		}
	}
}
