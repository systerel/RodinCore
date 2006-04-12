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
 * Implements the border translation
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings("unused")
public abstract class BorderTranslator extends IdentityTranslator {
	
	protected abstract Predicate translateBorder(RelationalPredicate pred, FormulaFactory ff);
		
	%include {Formula.tom}
	
	protected Expression translate(Expression expr, FormulaFactory ff) {
		return expr;
	}
	
	protected Predicate translate(Predicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
	    %match (Predicate pred) {
	    	RelationalPredicate(l, r)-> {
	    		Type intType = ff.makeIntegerType();
	    		if(`l.getType().equals(intType) && `r.getType().equals(intType)) {
	    			return translateBorder((RelationalPredicate)pred, ff);
	    		}
	    		else {
	    			return pred;
	    		}
	    	}
	       	_ -> {
	    		return super.translate(pred, ff);
	    	}
	    }
	}
}