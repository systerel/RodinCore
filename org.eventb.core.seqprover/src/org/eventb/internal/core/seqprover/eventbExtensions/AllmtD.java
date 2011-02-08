/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;

/**
 * Reasoner that instantiates a universally quantified implication and performs
 * a modus tollens on it in one step.
 * 
 * <p>
 * This reasoner reuses the input from {@link AllD.Input}.
 * </p>
 * 
 * @author "Thomas Muller"
 */
public class AllmtD extends AllD implements IVersionedReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".allmtD";
	private static int VERSION = 0;
	
	private static String display = "∀ hyp mt";
	
	@Override
	public int getVersion() {
		return VERSION;
	}

	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@Override
	protected String getDisplayedRuleName() {
		return display;
	}
	
	@Override
	protected String checkInstantiations(Expression[] instantiations,
			BoundIdentDecl[] decls) {
		for (int i = 0; i < instantiations.length; i++) {
			if (instantiations[i] == null)
				return "Missing instantiation for " + decls[i];
		}
		return null;
	}
	
	/**
	 * H⊢WD(E)   H,WD(E)⊢[x≔E]¬ Q   H,WD(E),[x≔E]¬ P ⊢ G
	 * -----------------------------------------------
	 * H,  ∀x· P ⇒ Q ⊢ G
	 */
	@ProverRule("FORALL_INST_MT")
	protected IAntecedent[] getAntecedents(DLib lib, Set<Predicate> WDpreds,
			Predicate univHyp, Predicate instantiatedPred) {

		assert instantiatedPred != null;
		assert Lib.isImp(instantiatedPred);
		final Predicate impLeft = Lib.impLeft(instantiatedPred);
		final Predicate impRight = Lib.impRight(instantiatedPred);

		final IAntecedent[] antecedents = new IAntecedent[3];
		// Well definedness condition : H⊢WD(E)
		{
			antecedents[0] = ProverFactory.makeAntecedent(
					lib.makeConj(WDpreds), null, getDeselectAction(univHyp));
		}
		// The instantiated and negated impRight to goal : H,WD(E)⊢[x≔E]¬ Q
		{
			final Predicate negImpRight = lib.makeNeg(impRight);
			final Set<Predicate> addedHyps = new LinkedHashSet<Predicate>(
					WDpreds);
			final Set<Predicate> toDeselect = new LinkedHashSet<Predicate>(
					WDpreds);
			antecedents[1] = ProverFactory
					.makeAntecedent(
							negImpRight,
							addedHyps,
							toDeselect,
							Lib.NO_FREE_IDENT,
							Collections
									.<IHypAction> singletonList(getDeselectAction(univHyp)));
		}

		// The instantiated continuation : H,WD(E),[x≔E]¬ P ⊢ G
		{
			final Set<Predicate> addedHyps = new LinkedHashSet<Predicate>(
					WDpreds);
			final Predicate negImpLeft = lib.makeNeg(impLeft);
			addedHyps.add(negImpLeft);
			final Set<Predicate> toDeselect = new LinkedHashSet<Predicate>(
					WDpreds);
			antecedents[2] = ProverFactory
					.makeAntecedent(
							null,
							addedHyps,
							toDeselect,
							Lib.NO_FREE_IDENT,
							Collections
									.<IHypAction> singletonList(getDeselectAction(univHyp)));
		}
		return antecedents;
	}
	
}