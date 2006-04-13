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
	
	protected Predicate translateArithmeticBorder(RelationalPredicate pred, FormulaFactory ff) {
		return pred;
	}
	
	protected Predicate translateSetBorder(RelationalPredicate pred, FormulaFactory ff) {
		return pred;
	}
		
	%include {Formula.tom}
	
	protected Expression translate(Expression expr, FormulaFactory ff) {
		return expr;
	}
	
	protected Predicate translate(Predicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
	    %match (Predicate pred) {
	    	RelationalPredicate(l, r)-> {
	    		Type intType = ff.makeIntegerType();
	    		
	    		if(`l.getType().equals(intType) && `r.getType().equals(intType))
	    			return translateArithmeticBorder((RelationalPredicate)pred, ff);
	    			
	    		if(`l.getType().getBaseType() != null && `r.getType().getBaseType() != null)
	    			return translateSetBorder((RelationalPredicate)pred, ff);

    			return pred;
	    	}
	       	_ -> {
	    		return super.translate(pred, ff);
	    	}
	    }
	}
}