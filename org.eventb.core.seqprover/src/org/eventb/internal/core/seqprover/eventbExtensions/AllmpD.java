/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - deselect WD predicate and used hypothesis
 *     Systerel - deselect WD pred and used hyp in 2 first antecedents (ver 0)
 *     Systerel - deselect WD predicate only if not already selected
 *     Systerel - refactored to reuse code from AllD
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;

/**
 * Reasoner that instantiates a universally quantified implication and performs
 * a modus ponens on it in one step.
 * 
 * <p>
 * This reasoner reuses the input from {@link AllD.Input}.
 * </p>
 * 
 * @author Farhad Mehta
 *
 */
public class AllmpD extends AllD implements IVersionedReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".allmpD";
	private static final int VERSION = 0;

	private static final String display = "∀ hyp mp";

	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public int getVersion() {
		return VERSION;
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
	 * H⊢WD(E)   H,WD(E)⊢[x≔E]P   H,WD(E),[x≔E]Q ⊢ G
	 * -----------------------------------------------
	 * H,  ∀x· P ⇒ Q ⊢ G
	 */
	@ProverRule("FORALL_INST_MP")
	protected IAntecedent[] getAntecedents(DLib lib, Set<Predicate> WDpreds,
			Predicate univHyp, Predicate instantiatedPred) {
		assert instantiatedPred != null;
		assert Lib.isImp(instantiatedPred);
		final Predicate impLeft = Lib.impLeft(instantiatedPred);
		final Predicate impRight = Lib.impRight(instantiatedPred);

		final IAntecedent[] antecedents = new IAntecedent[3];
		// Well definedness condition : H ⊢ WD(E)
		{
			antecedents[0] = makeAntecedent(lib.makeConj(WDpreds), null,
					getDeselectAction(univHyp));
		}
		// The instantiated impLeft to goal : H,WD(E) ⊢[x≔E]P
		{
			final Set<Predicate> addedHyps = new LinkedHashSet<Predicate>(
					WDpreds);
			final Set<Predicate> toDeselect = new LinkedHashSet<Predicate>(
					WDpreds);
			antecedents[1] = makeAntecedent(
					impLeft,
					addedHyps,
					toDeselect,
					Lib.NO_FREE_IDENT,
					Collections
							.<IHypAction> singletonList(getDeselectAction(univHyp)));
		}
		// The instantiated continuation : H,WD(E),[x≔E]Q ⊢ G
		{
			final Set<Predicate> addedHyps = new LinkedHashSet<Predicate>(
					WDpreds);
			final Set<Predicate> toDeselect = new LinkedHashSet<Predicate>(
					WDpreds);
			addedHyps.addAll(Lib.breakPossibleConjunct(impRight));
			antecedents[2] = makeAntecedent(
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
