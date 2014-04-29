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

import java.util.ArrayList;
import java.util.Collection;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.IRewriteHypAction;

/**
 * Default implementation of {@link IRewriteHypAction}.
 */
public class RewriteHypAction extends ForwardInfHypAction implements
		IRewriteHypAction {

	// TODO composition instead of inheritance

	private final Collection<Predicate> disappearingHyps;

	public RewriteHypAction(Collection<Predicate> hyps,
			FreeIdentifier[] addedIdents, Collection<Predicate> inferredHyps,
			Collection<Predicate> disappearingHyps) {
		super(hyps, addedIdents, inferredHyps);
		assert !disappearingHyps.isEmpty();
		assert hyps.containsAll(disappearingHyps);
		this.disappearingHyps = new ArrayList<Predicate>(disappearingHyps);
	}

	@Override
	public String getActionType() {
		return IRewriteHypAction.ACTION_TYPE;
	}

	@Override
	public Collection<Predicate> getDisappearingHyps() {
		return disappearingHyps;
	}

	@Override
	public IInternalProverSequent perform(IInternalProverSequent sequent) {
		final IInternalProverSequent result = sequent.performRewrite(hyps,
				addedIdents, inferredHyps, disappearingHyps);
		skipped = (result == sequent);
		return result;
	}

	@Override
	public IHypAction translate(FormulaFactory factory) {
		final IForwardInfHypAction trSuper = (IForwardInfHypAction) super
				.translate(factory);
		final Collection<Predicate> trDisapHyps = new ArrayList<Predicate>(
				disappearingHyps.size());
		for (Predicate hyp : disappearingHyps) {
			trDisapHyps.add(hyp.translate(factory));
		}

		return new RewriteHypAction(trSuper.getHyps(),
				trSuper.getAddedFreeIdents(), trSuper.getInferredHyps(),
				trDisapHyps);
	}
}
