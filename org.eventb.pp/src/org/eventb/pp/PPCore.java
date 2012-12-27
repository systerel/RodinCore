/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - published newPP tactic
 *******************************************************************************/
package org.eventb.pp;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.pp.PPInput;
import org.eventb.internal.pp.PPReasoner;
import org.eventb.internal.pp.core.ClauseDispatcher;
import org.eventb.internal.pp.core.ClauseSimplifier;
import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.provers.casesplit.CaseSplitter;
import org.eventb.internal.pp.core.provers.equality.EqualityProver;
import org.eventb.internal.pp.core.provers.predicate.PredicateProver;
import org.eventb.internal.pp.core.provers.seedsearch.SeedSearchProver;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.predicate.AbstractContext;
import org.osgi.framework.BundleContext;

/**
 * @since 0.5
 */
public class PPCore extends Plugin {

	public static final String PLUGIN_ID = "org.eventb.pp"; //$NON-NLS-1$
	
	/**
	 * debugging/tracing option names
	 */
	private static final String REASONER_TRACE = PLUGIN_ID + "/debug/reasoner"; //$NON-NLS-1$

	private static final String LOADER_PHASE1_TRACE = PLUGIN_ID + "/debug/loader/phase1"; //$NON-NLS-1$
	private static final String LOADER_PHASE2_TRACE = PLUGIN_ID + "/debug/loader/phase2"; //$NON-NLS-1$
	
	private static final String PROVER_TRACE = PLUGIN_ID + "/debug/prover"; //$NON-NLS-1$
	private static final String PROVER_STRATEGY_TRACE = PLUGIN_ID + "/debug/prover/strategy"; //$NON-NLS-1$
	private static final String PROVER_INFERENCE_TRACE = PLUGIN_ID + "/debug/prover/predicate"; //$NON-NLS-1$
	private static final String PROVER_CASESPLIT_TRACE = PLUGIN_ID + "/debug/prover/casesplit"; //$NON-NLS-1$
	private static final String PROVER_SIMPLIFICATION_TRACE = PLUGIN_ID + "/debug/prover/simplification"; //$NON-NLS-1$
	private static final String PROVER_DUMPING_TRACE = PLUGIN_ID + "/debug/prover/dumping"; //$NON-NLS-1$
	private static final String PROVER_SEEDSEARCH_TRACE = PLUGIN_ID + "/debug/prover/seedsearch"; //$NON-NLS-1$
	private static final String PROVER_EQUALITY_TRACE = PLUGIN_ID + "/debug/prover/equality"; //$NON-NLS-1$

	/**
	 * The shared instance.
	 */
	private static PPCore plugin;
	
	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		enableAssertions();
		if (isDebugging())
			configureDebugOptions();
	}

	/**
	 * Enables Java assertion checks for this plug-in.
	 */
	private void enableAssertions() {
		getClass().getClassLoader().setDefaultAssertionStatus(true);
	}

	/**
	 * Process debugging/tracing options coming from Eclipse.
	 */
	private void configureDebugOptions() {
		PPReasoner.DEBUG = parseOption(REASONER_TRACE);
		AbstractContext.setDebugFlag(parseOption(LOADER_PHASE1_TRACE));
		ClauseBuilder.DEBUG = parseOption(LOADER_PHASE2_TRACE);
		PPProof.DEBUG = parseOption(PROVER_TRACE);
		ClauseDispatcher.DEBUG = parseOption(PROVER_STRATEGY_TRACE);
		PredicateProver.DEBUG = parseOption(PROVER_INFERENCE_TRACE);
		CaseSplitter.DEBUG = parseOption(PROVER_CASESPLIT_TRACE);
		Dumper.DEBUG = parseOption(PROVER_DUMPING_TRACE);
		ClauseSimplifier.DEBUG = parseOption(PROVER_SIMPLIFICATION_TRACE);
		SeedSearchProver.DEBUG = parseOption(PROVER_SEEDSEARCH_TRACE);
		EqualityProver.DEBUG = parseOption(PROVER_EQUALITY_TRACE);
	}

	private static boolean parseOption(String key) {
		final String option = Platform.getDebugOption(key);
		return "true".equalsIgnoreCase(option); //$NON-NLS-1$
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}
	
	/**
	 * Logs an error message to the Rodin core plug-in log.
	 * 
	 * @param exc
	 *            a low-level exception, or <code>null</code> if not
	 *            applicable
	 * @param message
	 *            a message describing the error
	 */
	public static void log(String message) {
		IStatus status= new Status(
			IStatus.ERROR, 
			PPCore.PLUGIN_ID,
			Status.OK,
			message,null); 
		PPCore.plugin.getLog().log(status);
	}
	
	
	/**
	 * Returns a tactic that calls newPP with the given parameters.
	 * 
	 * @param restricted
	 *            <code>true</code> iff only selected hypotheses should be
	 *            considered
	 * @param timeout
	 *            timeout in milliseconds
	 * @param maxSteps
	 *            maximum number of steps after which PP stops or -1 for no
	 *            limit
	 * @return a tactic that calls PP with the given parameters
	 */
	public static ITactic newPP(boolean restricted, long timeout, int maxSteps) {
		return BasicTactics.reasonerTac(
				new PPReasoner(),
				new PPInput(restricted,timeout,maxSteps));
	}
	
}
