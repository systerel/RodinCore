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
		return makeAssociativePredicate(ff, tag, Arrays.asList(new Predicate[]{left, right}), loc);
	}
	
	public static Predicate makeAssociativePredicate(
			FormulaFactory ff, int tag, List<Predicate> preds, SourceLocation loc) {
		LinkedList<Predicate> childs = new LinkedList<Predicate>();
		
		for(Predicate pred: preds) {
			if(pred.getTag() == tag) {
				for(Predicate child: ((AssociativePredicate)pred).getChildren()) {
					childs.add(child);
				}
			}
			else{
				childs.add(pred);
			}
		}
		if(childs.size() == 1) return childs.getFirst();
		else
			return ff.makeAssociativePredicate(tag, childs, loc);
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

		DecomposedQuant forall = new DecomposedQuant(ff);
		Expression x = forall.addQuantifier(value.getType(), loc);
    	return forall.makeQuantifiedPredicate(
    		Formula.FORALL,
    		ff.makeBinaryPredicate(
    			Formula.LIMP,
    			ff.makeRelationalPredicate(
    				Formula.IN,
    				x,
    				forall.push(set),
    				loc),
    			ff.makeRelationalPredicate(
    				relationTag,
    				forall.push(value),
    				x,
    				loc),
    			loc),
    		loc);	   
	}
	
	public static Predicate makeLessThanExtremumPredicate(
			FormulaFactory ff, Expression value, Expression set, int relationTag, SourceLocation loc) {

		DecomposedQuant exists = new DecomposedQuant(ff);
		Expression x = exists.addQuantifier(value.getType(), loc);
    	return exists.makeQuantifiedPredicate(
    		Formula.EXISTS,
    		ff.makeBinaryPredicate(
    			Formula.LAND,
    			ff.makeRelationalPredicate(
    				Formula.IN,
    				x,
    				exists.push(set),
    				loc),
    			ff.makeRelationalPredicate(
    				relationTag,
    				exists.push(value),
    				x,
    				loc),
    			loc),
    		loc);	   
	}
	
	public static Predicate makeSimplifiedAssociativePredicate(
			FormulaFactory ff, int tag, List<Predicate> children, Predicate neutral, 
			Predicate determinant, SourceLocation loc, Predicate oldPredicate) {
		LinkedList<Predicate> childs = new LinkedList<Predicate>();
		
		boolean hasChanged = false;
		
	
		for(Predicate child: children) {
			if(child.equals(determinant))
				return determinant;
			else if(child.equals(neutral))
				hasChanged = true;
			else
				childs.add(child);
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

}
