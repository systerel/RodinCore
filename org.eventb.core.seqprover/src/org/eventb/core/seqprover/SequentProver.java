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
package org.eventb.core.seqprover;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eventb.core.seqprover.xprover.XProverReasoner;
import org.eventb.internal.core.seqprover.AutoTacticRegistry;
import org.eventb.internal.core.seqprover.ProverChecks;
import org.eventb.internal.core.seqprover.ProverSequent;
import org.eventb.internal.core.seqprover.ReasonerRegistry;
import org.eventb.internal.core.seqprover.eventbExtensions.MembershipGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewriterImpl;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * @since 1.0
 */
public class SequentProver extends Plugin {

	public static final String PLUGIN_ID = "org.eventb.core.seqprover"; //$NON-NLS-1$

	/**
	 * debugging/tracing option names
	 */
	private static final String SEQPROVER_TRACE = PLUGIN_ID
			+ "/debug/seqProver"; //$NON-NLS-1$
	private static final String PROVER_SEQUENT_TRACE = PLUGIN_ID
			+ "/debug/proverSequent"; //$NON-NLS-1$
	private static final String PROVER_CHECKS_TRACE = PLUGIN_ID
			+ "/debug/proverChecks"; //$NON-NLS-1$
	private static final String REASONER_REGISTRY_TRACE = PLUGIN_ID
			+ "/debug/reasonerRegistry"; //$NON-NLS-1$	
	private static final String TACTIC_REGISTRY_TRACE = PLUGIN_ID
			+ "/debug/tacticRegistry"; //$NON-NLS-1$
	private static final String XPROVER_TRACE = PLUGIN_ID
			+ "/debug/xProver"; //$NON-NLS-1$
	private static final String AUTO_REWRITER_TRACE = PLUGIN_ID
			+ "/debug/autoRewriter"; //$NON-NLS-1$
	private static final String MEMBERSHIP_GOAL_TRACE = PLUGIN_ID
			+ "/debug/mbGoal"; //$NON-NLS-1$
	
	/**
	 * The shared instance.
	 */
	private static SequentProver plugin;

	/**
	 * Debug flag for <code>SEQPROVER_TRACE</code>
	 */
	private static boolean DEBUG;

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		enableAssertions();
		if (isDebugging())
			configureDebugOptions();
	}

	/**
	 * Enable Java assertion checks for this plug-in.
	 */
	private void enableAssertions() {
		getClass().getClassLoader().setDefaultAssertionStatus(true);
	}

	/**
	 * Process debugging/tracing options coming from Eclipse.
	 */
	private void configureDebugOptions() {
		SequentProver.DEBUG = parseOption(SEQPROVER_TRACE);
		ProverSequent.DEBUG = parseOption(PROVER_SEQUENT_TRACE);
		ProverChecks.DEBUG = parseOption(PROVER_CHECKS_TRACE);
		ReasonerRegistry.DEBUG = parseOption(REASONER_REGISTRY_TRACE);
		AutoTacticRegistry.DEBUG = parseOption(TACTIC_REGISTRY_TRACE);
		XProverReasoner.DEBUG = parseOption(XPROVER_TRACE);
		AutoRewriterImpl.DEBUG = parseOption(AUTO_REWRITER_TRACE);
		MembershipGoal.DEBUG = parseOption(MEMBERSHIP_GOAL_TRACE);
	}

	private static boolean parseOption(String key) {
		final String option = Platform.getDebugOption(key);
		return "true".equalsIgnoreCase(option); //$NON-NLS-1$
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static SequentProver getDefault() {
		return plugin;
	}

	/**
	 * Returns the Reasoner registry managed by the sequent prover
	 * 
	 * @see IReasonerRegistry
	 * 
	 * @return the Reasoner registry managed by the sequent prover
	 */
	public static IReasonerRegistry getReasonerRegistry() {
		return ReasonerRegistry.getReasonerRegistry();
	}

	/**
	 * Returns the AutoTactic registry managed by the sequent prover
	 * 
	 * @see IAutoTacticRegistry
	 * 
	 * @return the AutoTactic registry managed by the sequent prover
	 */
	public static IAutoTacticRegistry getAutoTacticRegistry() {
		return AutoTacticRegistry.getTacticRegistry();
	}

	/**
	 * @deprecated use {@link #getAutoTacticRegistry()} instead
	 */
	public static IAutoTacticRegistry getTacticRegistry() {
		return AutoTacticRegistry.getTacticRegistry();
	}

	/**
	 * Prints the given message to the console in case the debug flag is
	 * switched on
	 * 
	 * @param message
	 *            The message to print out to the console
	 */
	public static void debugOut(String message) {
		if (DEBUG)
			System.out.println(message);
	}

}
