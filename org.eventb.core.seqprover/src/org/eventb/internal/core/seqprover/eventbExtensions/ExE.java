/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Arrays;
import java.util.Collections;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;

/**
 * @author fmehta
 *
 * @deprecated use the reasoner ExF instead since it generates a forward inference instead
 */
@Deprecated
public class ExE extends HypothesisReasoner{
	
	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".exE";
	
	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule("XST_L")
	@Override
	protected IAntecedent[] getAntecedents(IProverSequent sequent,
			Predicate pred) throws IllegalArgumentException {

		if (pred == null) {
			throw new IllegalArgumentException("Null hypothesis");
		}
		if (!Lib.isExQuant(pred)) {
			throw new IllegalArgumentException(
					"Hypothesis is not existentially quantified: " + pred);
		}

		final QuantifiedPredicate ExQ = (QuantifiedPredicate) pred;
		final BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(ExQ);
		
		// The type environment is cloned since makeFresh.. adds directly to the
		// given type environment
		// TODO : Change implementation
		final FormulaFactory ff = sequent.getFormulaFactory();
		final ITypeEnvironmentBuilder newTypenv = ff.makeTypeEnvironment();
		newTypenv.addAll(sequent.typeEnvironment());
		final FreeIdentifier[] freeIdents = newTypenv
				.makeFreshIdentifiers(boundIdentDecls);
		
		Predicate instantiatedEx = ExQ.instantiate(freeIdents, ff);
		assert instantiatedEx.isTypeChecked();
		
		final IHypAction action = ProverFactory.makeDeselectHypAction(Arrays.asList(pred));
		return new IAntecedent[] {
				ProverFactory.makeAntecedent(
				sequent.goal(),
				Lib.breakPossibleConjunct(instantiatedEx),
				freeIdents,
				Collections.singletonList(action))
		};
	}

	@Override
	protected String getDisplay(Predicate pred) {
		return "∃ hyp";
	}

}
