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
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites.Level;

/**
 * Basic manual rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class RemoveInclusionUniversalRewriterImpl extends AutoRewriterImpl {

	public RemoveInclusionUniversalRewriterImpl() {
		super(Level.L0);
	}

	%include {FormulaV2.tom}
	
	@ProverRule("DERIV_SUBSETEQ")
	@Override
	public Predicate rewrite(RelationalPredicate predicate) {
		Predicate newPredicate = super.rewrite(predicate);
		if (!newPredicate.equals(predicate))
			return newPredicate;
		FormulaFactory ff = predicate.getFactory();
	    %match (Predicate predicate) {
	    	    	
	    	/**
	    	 * Set Theory: S ⊆ T == U ∖ T ⊆ U ∖ S  
	    	 *                          (where both S and T are of type ℙ(U)) 
	    	 */
	    	SubsetEq(S, T) -> {
				Type type = `S.getType();
				Type baseType = type.getBaseType();
				Expression U = baseType.toExpression(ff);
				Expression left = ff.makeBinaryExpression(Expression.SETMINUS,
						U, `T, null);
				Expression right = ff.makeBinaryExpression(Expression.SETMINUS,
						U, `S, null);
				return ff.makeRelationalPredicate(Predicate.SUBSETEQ, left,
						right, null);
	    	}
	    }
	    return predicate;
	}

}
