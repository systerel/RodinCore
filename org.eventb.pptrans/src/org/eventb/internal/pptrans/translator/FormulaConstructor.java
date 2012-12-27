/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pptrans.translator;

import java.util.*;

import org.eventb.core.ast.*;


/**
 * @author mkonrad
 * Some helper methods for constructing formulas
 */
public abstract class FormulaConstructor {
	/**
	 * Flattens the children of an associative predicate.
	 * @param ff the Formula Factory
	 * @param tag either Formula.LAND or Formula.LOR
	 * @param left child one
	 * @param right child two
	 * @param loc the source location
	 * @return the new flattened predicate.
	 */
	public static Predicate makeAssociativePredicate(
			FormulaFactory ff, int tag, Predicate left, Predicate right, SourceLocation loc) {
		return makeAssociativePredicate(ff, tag, Arrays.asList(new Predicate[]{left, right}), loc);
	}
	
	/**
	 * Flattens the children of an associative predicate.
	 * @param ff the Formula Factory
	 * @param tag either Formula.LAND or Formula.LOR
	 * @param preds the children
	 * @param loc the source location
	 * @return the new flattened predicate.
	 */
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
	
	/**
	 * Flattens the children of an LAND predicate.
	 * @param ff the Formula Factory
	 * @param left child one
	 * @param right child two
	 * @param loc the source location
	 * @return the new flattened predicate.
	 */
	public static Predicate makeLandPredicate(
			FormulaFactory ff, Predicate left, Predicate right, SourceLocation loc) {
		return makeAssociativePredicate(ff, Formula.LAND, left, right, loc);
	}
	
	/**
	 * Flattens the children of an LOR predicate.
	 * @param ff the Formula Factory
	 * @param preds the children
	 * @param loc the source location
	 * @return the new flattened predicate.
	 */
	public static Predicate makeLandPredicate(
			FormulaFactory ff, List<Predicate> preds, SourceLocation loc) {
		return makeAssociativePredicate(ff, Formula.LAND, preds, loc);
	}
	
	/**
	 * Flattens the children of an LOR predicate.
	 * @param ff the Formula Factory
	 * @param left child one
	 * @param right child two
	 * @param loc the source location
	 * @return the new flattened predicate.
	 */
	public static Predicate makeLorPredicate(
			FormulaFactory ff, Predicate left, Predicate right, SourceLocation loc) {
		return makeAssociativePredicate(ff, Formula.LOR, left, right, loc);
	}
	
	/**
	 * Flattens the children of an LOR predicate.
	 * @param ff the Formula Factory
	 * @param preds the children
	 * @param loc the source location
	 * @return the new flattened predicate.
	 */
	public static Predicate makeLorPredicate(
			FormulaFactory ff, List<Predicate> preds, SourceLocation loc) {
		return makeAssociativePredicate(ff, Formula.LOR, preds, loc);
	}
	
	/**
	 * Simplifies an associative predicate.
	 * @param ff the Formula Factory used
	 * @param tag either Formula.LAND or Formula.LOR
	 * @param children 
	 * @param neutral if an occurrence of this predicate is found, it is ignored.
	 * @param determinant If an occurrence of this predicate is found, this parameter is the result.
	 * @param loc the source location
	 * @param oldPredicate is the result if not null and if no occurrencce of determinant or netral is found.
	 * @return a new Predicate
	 */
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
