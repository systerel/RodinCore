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
package org.eventb.seqprover.core.perf.app.tactics;

import static java.util.Arrays.asList;
import static org.eventb.core.seqprover.SequentProver.getAutoTacticRegistry;
import static org.eventb.seqprover.core.perf.app.tactics.Tactics.getCombinator;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;

/**
 * Inserts tactics for running an external prover within an auto-tactic.
 * 
 * @author Laurent Voisin
 */
public class TacticInserter {

	// Auto-tactic registry
	private static final IAutoTacticRegistry REGISTRY = getAutoTacticRegistry();

	private static final ICombinatorDescriptor ATTEMPT_AFTER_LASSO = REGISTRY
			.getCombinatorDescriptor("org.eventb.core.seqprover.attemptAfterLasso");

	// Auto-tactic in which to insert
	private ICombinedTacticDescriptor autoTactic;

	// Tactic builder for the external prover
	private TacticBuilder builder;

	/*
	 * The identifier of the tactic just before which the external solver shall
	 * be inserted in the auto-tactic.
	 */
	private final String beforeTacticId;

	protected TacticInserter(ICombinedTacticDescriptor autoTactic,
			TacticBuilder builder, String beforeTacticId) {
		this.autoTactic = autoTactic;
		this.builder = builder;
		this.beforeTacticId = beforeTacticId;
	}

	/**
	 * Returns a new tactic descriptor for running an external prover with three
	 * different sets of hypotheses.
	 */
	public final ITacticDescriptor insert() {
		final ICombinatorDescriptor combinator = getCombinator(autoTactic);
		final List<ITacticDescriptor> children = autoTactic
				.getCombinedTactics();
		final List<ITacticDescriptor> smtDescs = makeTacticsToAdd();
		final List<ITacticDescriptor> newChildren = new ArrayList<ITacticDescriptor>(
				children);
		final int index = getInsertionPoint(newChildren);
		newChildren.addAll(index, smtDescs);
		return combinator.combine(newChildren, "auto " + builder.getId());
	}

	private int getInsertionPoint(List<ITacticDescriptor> descs) {
		final ITacticDescriptor tactic;
		tactic = REGISTRY.getTacticDescriptor(beforeTacticId);
		return descs.indexOf(tactic);
	}

	/*
	 * Returns three tactics for running the external prover : - with selected
	 * hypotheses only, like P0 - with selected hypotheses after a lasso, like
	 * P1 - with all visible hypotheses, like PP.
	 */
	private List<ITacticDescriptor> makeTacticsToAdd() {
		final ITacticDescriptor restricted = builder.makeTactic(true);
		return asList(restricted, //
				inLasso(restricted), //
				builder.makeTactic(false));
	}

	private static ITacticDescriptor inLasso(ITacticDescriptor desc) {
		return ATTEMPT_AFTER_LASSO.combine(asList(desc), "in lasso");
	}

}
