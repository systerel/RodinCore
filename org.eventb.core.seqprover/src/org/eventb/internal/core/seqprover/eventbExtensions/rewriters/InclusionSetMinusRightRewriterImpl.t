/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

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
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites.Level;

/**
 * Basic manual rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class InclusionSetMinusRightRewriterImpl extends AutoRewriterImpl {

	public InclusionSetMinusRightRewriterImpl() {
		super(Level.L0);
	}

	%include {FormulaV2.tom}

    @ProverRule("DERIV_SUBSETEQ_SETMINUS_R")	
	@Override
	public Predicate rewrite(RelationalPredicate predicate) {
		FormulaFactory ff = predicate.getFactory();
	    %match (Predicate predicate) {
		    	
	    	/**
             * DERIV_SUBSETEQ_SETMINUS_R
	    	 * Set Theory: S ⊆ A ∖ B == (S ⊆ A ∧ S ∩ B = ∅) 
	    	 */
	    	SubsetEq(S, SetMinus(A, B)) -> {
	    		Predicate [] childrenPred = new Predicate[2];
	    		childrenPred[0] = ff.makeRelationalPredicate(Predicate.SUBSETEQ,
	    				`S, `A, null);
	    		Expression [] children = new Expression[2];
	    		children[0] = `S;
	    		children[1] = `B;
	    		Expression inter = ff.makeAssociativeExpression(Expression.BINTER,
	    				children, null);
	    		Expression emptySet = ff.makeEmptySet(`S.getType(), null);
	    		childrenPred[1] = ff.makeRelationalPredicate(Predicate.EQUAL,
	    				inter, emptySet, null);  
	    		return ff.makeAssociativePredicate(Predicate.LAND, childrenPred,
	    				null);
	    	}

	    }
	    return predicate;
	}

}
