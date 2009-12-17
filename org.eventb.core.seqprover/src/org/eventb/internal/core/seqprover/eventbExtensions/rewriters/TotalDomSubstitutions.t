/*******************************************************************************
 * Copyright (c) 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored and used tactic applications
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.eventb.core.seqprover.IProverSequent;

@SuppressWarnings("unused")
public class TotalDomSubstitutions {
	
	private final Map<Expression, Set<Expression>> substitutions = new HashMap<Expression, Set<Expression>>();
	private final IProverSequent sequent;
	
	public TotalDomSubstitutions(IProverSequent sequent) {
		this.sequent = sequent;
	}
	
	public void computeSubstitutions() {
		for (Predicate hyp : sequent.visibleHypIterable()) {
			computeSubstitution(hyp);
		}
	}
	
	public Set<Expression> get(Expression expression) {
	    final Set<Expression> result = substitutions.get(expression);
	    if (result == null) {
	        return Collections.emptySet();
	    }
		return result;
	}
	
    private void addSubstitution(Expression from, Expression to) {
        Set<Expression> set = substitutions.get(from);
        if (set == null) {
            set = new HashSet<Expression>();
            substitutions.put(from, set);
        }
        set.add(to);
    }

	%include {FormulaV2.tom}
	
	// a function that computes substitutions if the matched function is replacable for dom(f)
	// null otherwise
	private void computeSubstitution(Predicate predicate)
	{
			%match (Predicate predicate) {
			/**
	         * Total function
	         */
			In(left,Tfun(left1,_))->
			{
						addSubstitution(`left, `left1);
			}
			
			/**
	         * Total injection
	         */
			In(left,Tinj(left1,_))->
			{
						addSubstitution(`left, `left1);
			}
			
			/**
	         * Total surjection
	         */
			In(left,Tsur(left1,_))->
			{
						addSubstitution(`left, `left1);
			}

			/**
	         * Bijection
	         */
			In(left,Tbij(left1,_))->
			{
						addSubstitution(`left, `left1);
			}
			
			/**
	         * Total relation
	         */
			In(left,Trel(left1,_))->
			{
						addSubstitution(`left, `left1);
			}
            
            /**
             * Total surjective relation
             */
            In(left,Strel(left1,_))->
            {
                        addSubstitution(`left, `left1);
            }
            
			}
	} 
	
}