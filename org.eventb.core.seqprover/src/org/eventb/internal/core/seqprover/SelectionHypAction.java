package org.eventb.internal.core.seqprover;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;


public class SelectionHypAction implements IInternalHypAction, ISelectionHypAction {

	private final String actionType;
	private final Collection<Predicate> hyps;

	private static final Set<Predicate> NO_HYPS = Collections.unmodifiableSet(new HashSet<Predicate>());
	
	/**
	 * @param actionType
	 * 			Must be the same as the action types defined in {@link ISelectionHypAction}
	 * @param hyps
	 */
	public SelectionHypAction(final String actionType, final Collection<Predicate> hyps) {
		super();
		assert hyps != null;
		this.actionType = actionType;
		this.hyps = hyps == null ? NO_HYPS : hyps;
	}

	public Collection<Predicate> getHyps() {
		return hyps;
	}

	public IInternalProverSequent perform(IInternalProverSequent seq) {
		if (actionType.equals(ISelectionHypAction.SELECT_ACTION_TYPE))
			return seq.selectHypotheses(hyps);
		if (actionType.equals(ISelectionHypAction.DESELECT_ACTION_TYPE))
			return seq.deselectHypotheses(hyps);
		if (actionType.equals(ISelectionHypAction.HIDE_ACTION_TYPE))
			return seq.hideHypotheses(hyps);
		if (actionType.equals(ISelectionHypAction.SHOW_ACTION_TYPE))
			return seq.showHypotheses(hyps);
		return seq;
	}

	public String getActionType() {
		return actionType;
	}

	public void processDependencies(ProofDependenciesBuilder proofDeps) {
		// Nothing to do
	}	

}
