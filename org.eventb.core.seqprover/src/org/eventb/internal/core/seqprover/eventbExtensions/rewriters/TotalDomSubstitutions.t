/*******************************************************************************
 * Copyright (c) 2009, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored and used tactic applications
 *     Systerel - added reference to original hypothesis
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
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
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
	
	// substitution map: function |-> ( substitute |-> hypothesis )
	private final Map<Expression, Map<Expression, Predicate>> substitutions = new HashMap<Expression, Map<Expression, Predicate>>();
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
	    final Map<Expression, Predicate> map = substitutions.get(expression);
	    if (map == null) {
	        return Collections.emptySet();
	    }
		return map.keySet();
	}

    public Predicate getNeededHyp(Expression expression, Expression substitute) {
        final Map<Expression, Predicate> map = substitutions.get(expression);
        if (map == null) {
            return null;
        }
        return map.get(substitute);
    }
    
    private void addSubstitution(Expression from, Expression to, Predicate hyp) {
    	Map<Expression, Predicate> map = substitutions.get(from);
        if (map == null) {
            map = new HashMap<Expression, Predicate>();
            substitutions.put(from, map);
        }
        map.put(to, hyp);
    }

	%include {FormulaV2.tom}
	
	// Adds a substitution for a relation whose domain is know
	private void computeSubstitution(Predicate predicate)
	{
		%match (Predicate predicate) {
			In(left, (Tfun|Tinj|Tsur|Tbij|Trel|Strel)(left1, _))->
			{
				addSubstitution(`left, `left1, predicate);
			}
			
		}
	} 
	
}
