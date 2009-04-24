/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
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
import org.eventb.core.seqprover.eventbExtensions.Lib;

/**
 * Basic automated rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class TypeRewriterImpl extends DefaultRewriter {

	public TypeRewriterImpl() {
		super(true, FormulaFactory.getDefault());
	}
		
	%include {FormulaV2.tom}
	
	@Override
	public Predicate rewrite(RelationalPredicate predicate) {
	    %match (Predicate predicate) {

			/**
	    	 * Set Theory 23: E ∈ Typ == ⊤ (where Typ is a type expression)
	    	 */
			In(_, Typ) -> {
				if (`Typ.isATypeExpression())
					return Lib.True;
				return predicate;			
			}
			
			/**
			 * Set Theory 21: Typ = ∅  ==  ⊥  (where Typ is a type expression)
			 */
			Equal(Typ, EmptySet()) -> {
				if (`Typ.isATypeExpression())
					return Lib.False;
				return predicate;
			}

			/**
			 * Set Theory 22: ∅ = Typ  ==  ⊥  (where Typ is a type expression)
			 */
			Equal(EmptySet(), Typ) -> {
				if (`Typ.isATypeExpression())
					return Lib.False;
				return predicate;
			}

	    }
	    return predicate;
	}

}
