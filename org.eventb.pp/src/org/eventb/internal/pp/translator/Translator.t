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
 * Implements the translator from set-theory to predicate calculus.
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings("unused")
public abstract class Translator {

	%include {Formula.tom}
	public static class QuantMapletBuilder {
	
		private Counter c = null;
		private LinkedList<BoundIdentDecl> identDecls;
		private Expression maplet;
	
		public void calculate(Type type, int offset, SourceLocation loc, FormulaFactory ff) {
			c = new Counter(offset);
			maplet = mapletOfType(type, loc, ff);
			identDecls = new LinkedList<BoundIdentDecl>();
			for( int i = offset; i < c.value(); i++) {
				identDecls.addLast(ff.makeBoundIdentDecl("x_" + i, loc));
			}
		}
		
		public Expression getMaplet() {
			return maplet;
		}
		
		public LinkedList<BoundIdentDecl> getIdentDecls() {
			return identDecls;
		}
		
		private Expression mapletOfType(Type type, SourceLocation loc, FormulaFactory ff) {
			%match (Type type) {
				PowerSetType (_) -> {
					return ff.makeBoundIdentifier(c.increment(), loc, `type);
				}
				ProductType (left, right) -> {
					return ff.makeBinaryExpression(
						Formula.MAPSTO, 
						mapletOfType(`left, loc, ff),
						mapletOfType(`right, loc, ff),
						loc);
				}
				type -> {
					return ff.makeBoundIdentifier(c.increment(), loc, `type);	
				}
			}
		}
	}
	/*
	private static Expression decomposeExpression(Expression expr, LinkedList<Type> identTypes, Counter c, FormulaFactory ff){
		QuantMapletBuilder mb = new QuantMapletBuilder();
		
		%match (Expression expr) {
			Cset(is, P, E) | Qunion(is, P, E) | Qinter(is, P, E) -> {
				List<BoundIdentDecl> identDecls = new LinkedList<BoundIdentDecl>();
				for (BoundIdentDecl decl: `is) {
					mb.calculate(decl.getType(), decl.getSourceLocation(), ff);
					identTypes.addFirst (decl.getType());
					c.add(mb.getIdentDecls().size());
					identDecls.addAll(mb.getIdentDecls());					
				}	
				QuantifiedExpression result = ff.makeQuantifiedExpression(
					expr.getTag(),
					identDecls,
					decomposePredicate(`P, identTypes, new Counter(c), ff),
					decomposeExpression(`E, identTypes, new Counter(c), ff),
					null,
					null);

				for (int i = 0; i < `is.length; i++) {
					identTypes.removeFirst();
				}
			}
			_ -> {
				return null;
			}
		}
	}
	
	private static Predicate decomposePredicate(Predicate P, LinkedList<Type> identTypes, Counter c, FormulaFactory ff) {
		%match (Predicate P) {
			ForAll(is, P) | Exists(is, P) -> {
				
			}
			_ -> {
				return null;
			}
		}
	}*/
	
	public static Predicate translateIn(Expression E, Expression right, SourceLocation loc, FormulaFactory ff) {
		QuantMapletBuilder mb = new QuantMapletBuilder();

		%match (Expression right) {
			FreeIdentifier(name) -> {
				/*TODO: Remove*/
				return ff.makeRelationalPredicate(
					Formula.IN,
					E,
					right,
					loc);
			}
			Pow(child) -> {
				mb.calculate(E.getType(), 0, loc, ff);
	    		return ff.makeQuantifiedPredicate(
					Formula.FORALL,
					mb.getIdentDecls(),
					ff.makeBinaryPredicate(
						Formula.LIMP,
						translateIn(
								mb.getMaplet(),
								E,
								loc,
								ff),
						translateIn(
								mb.getMaplet(),
								`child,
								loc,
								ff), 
						loc),
				loc);
			}
			NATURAL() -> {
				return ff.makeRelationalPredicate(
					Formula.GE,
					E,
					ff.makeIntegerLiteral(new BigInteger("0"), loc),
					loc);
			}
			NATURAL1() -> {
				return ff.makeRelationalPredicate(
					Formula.GT,
					E,
					ff.makeIntegerLiteral(new BigInteger("0"), loc),
					loc);
			}
			INTEGER() -> {
				return  ff.makeLiteralPredicate(Formula.BTRUE, loc);	
			}
			Cset(is, P, F) -> {
				return ff.makeQuantifiedPredicate(
					Formula.EXISTS,
					`is,
					ff.makeAssociativePredicate(
						Formula.LAND,
						new Predicate[]{
							translate(`P, ff),
							translate(
								ff.makeRelationalPredicate(Formula.EQUAL, E,`F, loc),
								ff)},
						loc),
					loc);
			} 
			Qinter(is, P, F) -> {
				return ff.makeQuantifiedPredicate(
					Formula.FORALL,
					`is,
					ff.makeBinaryPredicate(
						Formula.LIMP,
						translate(`P, ff),
						translateIn(E, `F, loc,	ff),
						loc),
					loc);
			}
			Qunion(is, P, F) -> {
				return ff.makeQuantifiedPredicate(
					Formula.EXISTS,
					`is,
					ff.makeAssociativePredicate(
						Formula.LAND,
						new Predicate[]{
							translate(`P, ff),
							translateIn(E, `F, loc, ff)},
						loc),
					loc);
			}
			P -> {
				throw new AssertionError("no mapping for: " + `P);
	    	}
		}					
	}
	/*
	protected static Predicate translateEqv (Expression left, Expression right, 
		SourceLocation loc, FormulaFactory ff) {
		
	}*/
	public static Predicate translate(Predicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
	    %match (Predicate pred) {
	    	Land(children) -> {
	    		ArrayList<Predicate> newChildren = new ArrayList<Predicate>();
	    		for (Predicate child: `children) {
	    			newChildren.add(translate(child, ff));
	    		}
		    	if (newChildren.size() == 1) {
		    		return newChildren.get(0);
	    		} else {
		    		return ff.makeAssociativePredicate(Formula.LAND, 
		    				newChildren, loc);
	    		}
	    	}
	    	Lor(children) -> {
	    		ArrayList<Predicate> newChildren = new ArrayList<Predicate>();
	    		for (Predicate child: `children) {
	    			newChildren.add(translate(child, ff));
	    		}
		    	if (newChildren.size() == 1) {
		    		return newChildren.get(0);
	    		} else {
		    		return ff.makeAssociativePredicate(Formula.LOR, 
		    				newChildren, loc);
	    		}
	    	}/*
	    	Limp(left, right) -> {
	    		return pred;
	    	}*//*
	        Leqv(left, right) -> {
	        	return translateEqv (`left, `right, loc, ff);
	        }		*/    		    	
	    	In(left, right) -> {
	    		return translateIn (`left, `right, loc, ff);
	    	}
	    	(BTRUE | BFALSE) -> {
	    		return pred;
	    	}
	    	Not(P) -> {
	    		return ff.makeUnaryPredicate(Formula.NOT, translate(`P, ff), loc);
	    	}
	    	P -> {
	    		throw new AssertionError("No Predicate mapping for: " + `P);
	    	}
	    }
	}
}
