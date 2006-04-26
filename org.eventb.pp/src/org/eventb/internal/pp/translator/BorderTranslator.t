/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.translator;

import java.math.BigInteger;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;


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
	
	@Override	
	protected Expression translate(Expression expr, FormulaFactory ff) {
		return expr;
	}
	
	@Override
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