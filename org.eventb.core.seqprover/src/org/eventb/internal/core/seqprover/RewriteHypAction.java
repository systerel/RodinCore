/*******************************************************************************
 * Copyright (c) 2013, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import static org.eventb.core.seqprover.IHypAction.ISelectionHypAction.HIDE_ACTION_TYPE;

import java.util.Collection;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.IRewriteHypAction;

/**
 * Default implementation of {@link IRewriteHypAction}. We compose a forward
 * inference and a hide action into one, delegating to the appropriate action.
 */
public class RewriteHypAction implements IInternalHypAction, IRewriteHypAction {

	private final ForwardInfHypAction fwdInf;
	private final SelectionHypAction hide;

	public RewriteHypAction(Collection<Predicate> hyps,
			FreeIdentifier[] addedIdents, Collection<Predicate> inferredHyps,
			Collection<Predicate> disappearingHyps) {
		this(new ForwardInfHypAction(hyps, addedIdents, inferredHyps),
				new SelectionHypAction(HIDE_ACTION_TYPE, disappearingHyps));
	}

	private RewriteHypAction(ForwardInfHypAction fwdInf,
			SelectionHypAction hide) {
		assert !hide.getHyps().isEmpty();
		assert fwdInf.getHyps().containsAll(hide.getHyps());
		this.fwdInf = fwdInf;
		this.hide = hide;
	}

	@Override
	public FreeIdentifier[] getAddedFreeIdents() {
		return fwdInf.getAddedFreeIdents();
	}

	@Override
	public Collection<Predicate> getInferredHyps() {
		return fwdInf.getInferredHyps();
	}

	@Override
	public Collection<Predicate> getHyps() {
		return fwdInf.getHyps();
	}

	@Override
	public Collection<Predicate> getDisappearingHyps() {
		return hide.getHyps();
	}

	@Override
	public String getActionType() {
		return IRewriteHypAction.ACTION_TYPE;
	}

	@Override
	public IInternalProverSequent perform(IInternalProverSequent sequent) {
		final IInternalProverSequent result = sequent.performRewrite(getHyps(),
				getAddedFreeIdents(), getInferredHyps(), getDisappearingHyps());
		fwdInf.setSkipped(result == sequent);
		return result;
	}

	@Override
	public void processDependencies(ProofDependenciesBuilder proofDeps) {
		if (fwdInf.isSkipped()) {
			return;
		}
		fwdInf.processDependencies(proofDeps);
		hide.processDependencies(proofDeps);
	}

	@Override
	public IHypAction translate(FormulaFactory factory) {
		final ForwardInfHypAction trFwdInf = (ForwardInfHypAction) fwdInf
				.translate(factory);
		final SelectionHypAction trHide = (SelectionHypAction) hide
				.translate(factory);

		return new RewriteHypAction(trFwdInf, trHide);
	}
}
