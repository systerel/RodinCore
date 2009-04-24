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
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;

/**
 * Basic implementation for "finite maximum"
 */
@SuppressWarnings("unused")
public class FiniteMax extends AbstractEmptyInputReasoner {

	%include {FormulaV2.tom}
	
	public String getReasonerID() {
		return SequentProver.PLUGIN_ID + ".finiteMax";
	}
	
	@Override
	public boolean isApplicable(Predicate goal) {
	    %match (Predicate goal) {
			
			/**
	    	 * Set Theory: ∃n·(∀x·x ∈ S ⇒ x ≤ n)
	    	 */
			Exists(in, ForAll(ix, Limp(In(x, S), Le(x, n)))) -> {
				if (`in.length == 1 && `ix.length == 1) {
					if (Lib.isBoundIdentifier(`x) && Lib.isBoundIdentifier(`n)) {
						return (((BoundIdentifier) `x).getBoundIndex() == 0 && 
								((BoundIdentifier) `n).getBoundIndex() == 1 &&
								`S.getBoundIdentifiers().length == 0);
					}
				}
			}

			/**
	    	 * Set Theory: ∃n·(∀x·x ∈ S ⇒ n ≥ x)
	    	 */
			Exists(in, ForAll(ix, Limp(In(x, S), Ge(n, x)))) -> {
				if (`in.length == 1 && `ix.length == 1) {
					if (Lib.isBoundIdentifier(`x) && Lib.isBoundIdentifier(`n)) {
						return (((BoundIdentifier) `x).getBoundIndex() == 0 && 
								((BoundIdentifier) `n).getBoundIndex() == 1 &&
								`S.getBoundIdentifiers().length == 0);
					}
				}
			}
	    }
	    return false;
	}

	@Override
	protected String getDisplayName() {
		return "Existence of maximum using finite";
	}

	@Override
	protected IAntecedent[] getAntecedents(IProverSequent seq) {
		Predicate goal = seq.goal();
		
		Expression S = null;
	    %match (Predicate goal) {
			
			/**
	    	 * Set Theory: ∃n·(∀x·x ∈ S ⇒ x ≤ n)
	    	 */
			Exists(in, ForAll(ix, Limp(In(x, SS), Le(x, n)))) -> {
				if (`in.length == 1 && `ix.length == 1) {
					if (Lib.isBoundIdentifier(`x) && Lib.isBoundIdentifier(`n)) {
						if (((BoundIdentifier) `x).getBoundIndex() == 0 && 
								((BoundIdentifier) `n).getBoundIndex() == 1 &&
								`SS.getBoundIdentifiers().length == 0) {
							S = `SS;
						}
					}
				}
			}

			/**
	    	 * Set Theory: ∃n·(∀x·x ∈ S ⇒ n ≥ x)
	    	 */
			Exists(in, ForAll(ix, Limp(In(x, SS), Ge(n, x)))) -> {
				if (`in.length == 1 && `ix.length == 1) {
					if (Lib.isBoundIdentifier(`x) && Lib.isBoundIdentifier(`n)) {
						if (((BoundIdentifier) `x).getBoundIndex() == 0 && 
								((BoundIdentifier) `n).getBoundIndex() == 1 &&
								`SS.getBoundIdentifiers().length == 0) {
							S = `SS;
						}
					}
				}
			}
	    }

		if (S == null)
			return null;
			
		// There will be 1 antecidents
		IAntecedent[] antecidents = new IAntecedent[1];
		
		// finite(S)
		Predicate newGoal0 = ff.makeSimplePredicate(Predicate.KFINITE, S, null);
		antecidents[0] = ProverFactory.makeAntecedent(newGoal0);

		return antecidents;
	}
	
}
