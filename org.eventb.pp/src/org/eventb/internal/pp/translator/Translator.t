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
public class Translator extends IdentityTranslator {


	public static Predicate reduceToPredCalc(Predicate pred, FormulaFactory ff) {
		pred = IdentifierDecomposition.decomposeIdentifiers(pred, ff);
		return new Translator().translate(pred, ff);
	}
	
	%include {Formula.tom}
	
	protected Predicate translateIn(Expression E, Expression right, SourceLocation loc, FormulaFactory ff) {
		QuantMapletBuilder mb = new QuantMapletBuilder();

		%match (Expression right) {
			FreeIdentifier(name) -> {
				/*TODO: Remove.*/
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
			SetExtension(members) -> {
				if(`members.length == 0) {
					return ff.makeLiteralPredicate(Formula.BFALSE, loc);
				}
				else{
					LinkedList<Predicate> predicates = new LinkedList<Predicate>();
					for(Expression member: `members){
						predicates.add(
							ff.makeRelationalPredicate(Formula.EQUAL, E, member, loc));
								
					}
					if(predicates.size() == 1) {
						return translate(predicates.getFirst(), ff);
					}
					else{
						return translate(
							ff.makeAssociativePredicate(Formula.LAND, predicates, loc), ff);
					}
				}
			}
			P -> {
				throw new AssertionError("no mapping for: " + `P);
	    	}
		}					
	}
	
	protected Predicate translateEqv (Expression left, Expression right, 
		SourceLocation loc, FormulaFactory ff) {
		return null;
	}
	
	protected Predicate translate(Predicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
	    %match (Predicate pred) {/*
	    	Leqv(left, right) -> {
	        	return translateEqv (`left, `right, loc, ff);
	        }		   		*/    	
	    	In(left, right) -> {
	    		return translateIn (`left, `right, loc, ff);
	    	}
	    	_ -> {
	    		return super.translate(pred, ff);
	    	}
	    }
	}
}
