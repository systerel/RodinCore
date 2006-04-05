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
	private static AssociativePredicate makeAssociativePredicate(
			FormulaFactory ff, int tag, Predicate left, Predicate right, SourceLocation loc) {
		return makeAssociativePredicate(ff, tag, Arrays.asList(new Predicate[]{left, right}), loc);
	}
	
	private static AssociativePredicate makeAssociativePredicate(
			FormulaFactory ff, int tag, List<Predicate> preds, SourceLocation loc) {
		LinkedList<Predicate> childs = new LinkedList<Predicate>();
		for(Predicate pred: preds) {
			if(pred.getTag() == tag) {
				for(Predicate child: ((AssociativePredicate)pred).getChildren())
					childs.add(child);
			}
			else{
				childs.add(pred);
			}
		}
		return ff.makeAssociativePredicate(tag, childs, loc);		
	}
	

	public static AssociativePredicate makeLandPredicate(
			FormulaFactory ff, Predicate left, Predicate right, SourceLocation loc) {
		return makeAssociativePredicate(ff, Formula.LAND, left, right, loc);
	}
	
	public static AssociativePredicate makeLandPredicate(
			FormulaFactory ff, List<Predicate> preds, SourceLocation loc) {
		return makeAssociativePredicate(ff, Formula.LAND, preds, loc);
	}
	
	public static AssociativePredicate makeLorPredicate(
			FormulaFactory ff, Predicate left, Predicate right, SourceLocation loc) {
		return makeAssociativePredicate(ff, Formula.LOR, left, right, loc);
	}
	
	public static AssociativePredicate makeLorPredicate(
			FormulaFactory ff, List<Predicate> preds, SourceLocation loc) {
		return makeAssociativePredicate(ff, Formula.LOR, preds, loc);
	}
}
