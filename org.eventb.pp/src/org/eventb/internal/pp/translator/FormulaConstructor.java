/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.translator;

import java.util.*;

import org.eventb.core.ast.*;

public class FormulaConstructor {
	public static Predicate makeAssociativePredicate(
			FormulaFactory ff, int tag, Predicate left, Predicate right, SourceLocation loc) {
		return makeAssociativePredicate(ff, tag, Arrays.asList(new Predicate[]{left, right}), loc , null);
	}
	
	public static Predicate makeAssociativePredicate(
			FormulaFactory ff, int tag, List<Predicate> preds, SourceLocation loc) {
		return makeAssociativePredicate(ff, tag, preds, loc, null);
	}

	public static Predicate makeAssociativePredicate(
			FormulaFactory ff, int tag, List<Predicate> preds, SourceLocation loc,
			Predicate oldPredicate) {
		LinkedList<Predicate> childs = new LinkedList<Predicate>();
		
		boolean hasChanged = false;
		Predicate bTrue = ff.makeLiteralPredicate(Formula.BTRUE, loc);
		Predicate bFalse = ff.makeLiteralPredicate(Formula.BFALSE, loc);
		
		Predicate neutral = null, determinant = null;
		if(tag == Formula.LAND) {
			neutral  = bTrue; determinant = bFalse;			
		}
		else if(tag == Formula.LOR ) {
			neutral = bFalse; determinant = bTrue;
		}
		else { assert false : "expedted tag LAND or LOR"; }
		
		for(Predicate pred: preds) {
			if(pred.getTag() == tag) {
				for(Predicate child: ((AssociativePredicate)pred).getChildren()) {
					if(child.equals(determinant))
						return determinant;
					else if(!child.equals(neutral))
						childs.add(child);
				}
				hasChanged = true;
			}
			else{
				if(pred.equals(determinant))
					return determinant;
				else if(!pred.equals(neutral))
					childs.add(pred);
			}
		}
		if(childs.size() == 0) return neutral;
		else if(childs.size() == 1) return childs.getFirst();
		else {
			if(hasChanged || oldPredicate == null)
				return ff.makeAssociativePredicate(tag, childs, loc);
			else
				return oldPredicate;
		}
	}

	public static Predicate makeLandPredicate(
			FormulaFactory ff, Predicate left, Predicate right, SourceLocation loc) {
		return makeAssociativePredicate(ff, Formula.LAND, left, right, loc);
	}
	
	public static Predicate makeLandPredicate(
			FormulaFactory ff, List<Predicate> preds, SourceLocation loc) {
		return makeAssociativePredicate(ff, Formula.LAND, preds, loc);
	}
	
	public static Predicate makeLorPredicate(
			FormulaFactory ff, Predicate left, Predicate right, SourceLocation loc) {
		return makeAssociativePredicate(ff, Formula.LOR, left, right, loc);
	}
	
	public static Predicate makeLorPredicate(
			FormulaFactory ff, List<Predicate> preds, SourceLocation loc) {
		return makeAssociativePredicate(ff, Formula.LOR, preds, loc);
	}
	
	public static Predicate makeGreaterThanExtremumPredicate(
			FormulaFactory ff, Expression value, Expression set, int relationTag, SourceLocation loc) {
		QuantMapletBuilder mb = new QuantMapletBuilder();
		mb.calculate(value.getType(), loc, ff);
    	return ff.makeQuantifiedPredicate(
    		Formula.FORALL,
    		mb.X(),
    		ff.makeBinaryPredicate(
    			Formula.LIMP,
    			ff.makeRelationalPredicate(
    				Formula.IN,
    				mb.V(),
    				set.shiftBoundIdentifiers(mb.offset(), ff),
    				loc),
    			ff.makeRelationalPredicate(
    				relationTag,
    				value.shiftBoundIdentifiers(mb.offset(), ff),
    				mb.V(),
    				loc),
    			loc),
    		loc);	   
	}
	
	public static Predicate makeLessThanExtremumPredicate(
			FormulaFactory ff, Expression value, Expression set, int relationTag, SourceLocation loc) {
		QuantMapletBuilder mb = new QuantMapletBuilder();
		mb.calculate(value.getType(), loc, ff);
    	return ff.makeQuantifiedPredicate(
    		Formula.EXISTS,
    		mb.X(),
    		ff.makeBinaryPredicate(
    			Formula.LAND,
    			ff.makeRelationalPredicate(
    				Formula.IN,
    				mb.V(),
    				set.shiftBoundIdentifiers(mb.offset(), ff),
    				loc),
    			ff.makeRelationalPredicate(
    				relationTag,
    				value.shiftBoundIdentifiers(mb.offset(), ff),
    				mb.V(),
    				loc),
    			loc),
    		loc);	   
	}
}
