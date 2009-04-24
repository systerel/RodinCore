/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
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
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
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
import org.eventb.core.ast.expanders.Expanders;

/**
 * Automated rewriter for the Event-B sequent prover: expand the definition of
 * a partition predicate.
 */
@SuppressWarnings("unused")
public class PartitionRewriterImpl extends DefaultRewriter {

	public PartitionRewriterImpl() {
		super(true, FormulaFactory.getDefault());
	}
		
	%include {FormulaV2.tom}
	
	@Override
	public Predicate rewrite(MultiplePredicate predicate) {
	    %match (Predicate predicate) {

			/**
	    	 * Set Theory : partition(S, S1, S2, ..., Sn) == S = S1 ∪ S2 ∪ ... ∪ Sn ∧
	    	 *                                               S1 ∩ S2 = ø  ∧
	    	 *                                               S1 ∩ ... = ø ∧
	    	 *                                               ...          ∧
	    	 *                                               Sn-1 ∩ Sn = ø
	    	 *                                               (where S and Si are sets)
	    	 */
			Partition(_) -> {
				return Expanders.expandPARTITION(predicate, ff);
			}
			
	    }
	    return predicate;
	}

}
