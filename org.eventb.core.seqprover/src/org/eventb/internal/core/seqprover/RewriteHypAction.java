/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
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

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction.IRewriteHypAction;

/**
 * Default implementation of {@link IRewriteHypAction}.
 */
public class RewriteHypAction extends ForwardInfHypAction implements
		IRewriteHypAction {

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
		final IInternalProverSequent rewritten = super.perform(sequent);
		if (!skipped) {
			return rewritten.hideHypotheses(disappearingHyps);
		}
		return sequent;
	}

}
