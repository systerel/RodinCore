/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.internal.core.seqprover.proofSimplifier;

import static org.eventb.core.seqprover.IHypAction.ISelectionHypAction.HIDE_ACTION_TYPE;
import static org.eventb.core.seqprover.IHypAction.ISelectionHypAction.SELECT_ACTION_TYPE;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;

/**
 * @author Nicolas Beauger
 * 
 */
public class AntecedentSimplifier extends Simplifier<IAntecedent> {

	private static boolean hasType(IHypAction hypAction, String type) {
		return hypAction.getActionType().equals(type);
	}

	private final Set<Predicate> neededPreds;

	public AntecedentSimplifier(Set<Predicate> neededPreds) {
		this.neededPreds = neededPreds;
	}

	public IAntecedent simplify(IAntecedent antecedent, IProofMonitor monitor)
			throws CancelException {
		final List<IHypAction> hypActions = new ArrayList<IHypAction>(
				antecedent.getHypActions());
		final Set<Predicate> unneededHide = new LinkedHashSet<Predicate>();

		removeUnneededFwd(hypActions, unneededHide, monitor);
		checkCancel(monitor);

		removeUnneededHide(hypActions, unneededHide, monitor);
		checkCancel(monitor);

		removeUnneededSelect(hypActions, monitor);
		checkCancel(monitor);
		
		return makeAntecedent(antecedent.getGoal(), antecedent.getAddedHyps(),
				antecedent.getAddedFreeIdents(), hypActions);

	}

	public Set<Predicate> getNeededPreds() {
		return neededPreds;
	}

	private void removeUnneededHide(List<IHypAction> hypActions,
			Set<Predicate> unneededHide, IProofMonitor monitor)
			throws CancelException {
		final Iterator<IHypAction> iter = hypActions.iterator();
		while (iter.hasNext()) {
			final IHypAction hypAction = iter.next();
			if (hasType(hypAction, HIDE_ACTION_TYPE)) {
				final ISelectionHypAction selHypAction = (ISelectionHypAction) hypAction;
				final Collection<Predicate> hyps = selHypAction.getHyps();
				if (unneededHide.containsAll(hyps)) {
					iter.remove();
				}
			}
			checkCancel(monitor);
		}
	}

	private void removeUnneededFwd(List<IHypAction> hypActions,
			Set<Predicate> unneededHide, IProofMonitor monitor)
			throws CancelException {
		final Iterator<IHypAction> iter = hypActions.iterator();
		while (iter.hasNext()) {
			final IHypAction hypAction = iter.next();
			if (hypAction instanceof IForwardInfHypAction) {
				final IForwardInfHypAction fwdHypAction = (IForwardInfHypAction) hypAction;
				if (!keepFwdHypAction(fwdHypAction)) {
					unneededHide.addAll(fwdHypAction.getHyps());
					iter.remove();
				} else {
					neededPreds.addAll(fwdHypAction.getHyps());
				}
			}
			checkCancel(monitor);
		}
	}

	
	// keep the forward action iff one or more inferred hyps are needed
	private boolean keepFwdHypAction(IForwardInfHypAction fwdHypAction) {
		return containsNeeded(fwdHypAction.getInferredHyps());
	}

	
	
	private void removeUnneededSelect(List<IHypAction> hypActions,
			IProofMonitor monitor) throws CancelException {
		final Iterator<IHypAction> iter = hypActions.iterator();
		while (iter.hasNext()) {
			final IHypAction hypAction = iter.next();
			if (hasType(hypAction, SELECT_ACTION_TYPE)) {
				final ISelectionHypAction selHypAction = (ISelectionHypAction) hypAction;
				
				if (!keepSelHypAction(selHypAction)) {
					iter.remove();
				}
			}
			checkCancel(monitor);
		}
	}

	// keep the selection action iff one or more selected hyps are needed
	private boolean keepSelHypAction(ISelectionHypAction selHypAction) {
		return containsNeeded(selHypAction.getHyps());
	}

	private boolean containsNeeded(Collection<Predicate> preds) {
		for (Predicate pred : preds) {
			if (neededPreds.contains(pred)) {
				return true;
			}
		}
		return false;
	}
}
