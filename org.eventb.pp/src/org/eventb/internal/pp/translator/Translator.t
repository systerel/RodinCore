/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.translator;

import java.util.ArrayList;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;

/**
 * Implements the translator from set-theory to predicate calculus.
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings("unused")
public abstract class Translator {

	%include {Formula.tom}
	
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
			// TODO implement the rest
	    	_ -> {
	    		return pred;
	    	}
	    }
	}
	
}
