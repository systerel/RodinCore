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

import static org.eventb.core.EventBPlugin.getAutoPostTacticManager;
import static org.eventb.core.seqprover.SequentProver.getAutoTacticRegistry;

import org.eventb.core.preferences.autotactics.IAutoPostTacticManager;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;

/**
 * Builds tactics that can be used to run the Event-B sequent prover in
 * automated mode. All built tactics are based on the
 * <code>Default Auto Tactic</code> where some external provers have been
 * inserted. They allow to compare the performances of these external provers in
 * automated mode.
 * 
 * @author Laurent Voisin
 */
public abstract class Tactics {

	/**
	 * Returns a tactic running just provers embedded within the core Rodin
	 * platform.
	 */
	public static ITacticDescriptor bareRodinTactic() {
		return DEFAULT_AUTO;
	}

	/**
	 * Returns a tactic running the two Atelier B provers.
	 * 
	 * @return an auto-tactic with both Atelier B provers.
	 */
	public static ITacticDescriptor mbGoalTactic() {
		final TacticBuilder builder = new MbGoalTacticBUilder();
		return new TacticInserter((ICombinedTacticDescriptor) DEFAULT_AUTO,
				builder, "org.eventb.core.seqprover.partitionRewriteTac")
				.insert();
	}

	/**
	 * Returns the tactic descriptor for the "Default Auto Tactic" from the
	 * Event-B core plug-in.
	 * 
	 * @return the "Default Auto Tactic" descriptor
	 */
	public static ITacticDescriptor getDefaultAuto() {
		final IAutoPostTacticManager manager = getAutoPostTacticManager();
		final IAutoTacticPreference pref = manager.getAutoTacticPreference();
		return pref.getDefaultDescriptor();
	}

	// Auto-tactic registry
	protected static final IAutoTacticRegistry REGISTRY = getAutoTacticRegistry();

	// The "Default Auto Tactic"
	private static final ITacticDescriptor DEFAULT_AUTO = getDefaultAuto();

	protected static ICombinatorDescriptor getCombinator(
			ICombinedTacticDescriptor desc) {
		final String combinatorId = desc.getCombinatorId();
		return REGISTRY.getCombinatorDescriptor(combinatorId);
	}

}
