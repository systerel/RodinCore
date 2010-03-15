/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
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
//	private static final String LOADER_TRACE = PLUGIN_ID + "/debug/loader"; //$NON-NLS-1$
	private static final String LOADER_PHASE1_TRACE = PLUGIN_ID + "/debug/loader/phase1"; //$NON-NLS-1$
	private static final String lOADER_PHASE2_TRACE = PLUGIN_ID + "/debug/loader/phase2"; //$NON-NLS-1$
	
	private static final String PROVER_TRACE = PLUGIN_ID + "/debug/prover"; //$NON-NLS-1$
	private static final String PROVER_STRATEGY_TRACE = PLUGIN_ID + "/debug/prover/strategy"; //$NON-NLS-1$
	private static final String PROVER_INFERENCE_TRACE = PLUGIN_ID + "/debug/prover/predicate"; //$NON-NLS-1$
	private static final String PROVER_CASESPLIT_TRACE = PLUGIN_ID + "/debug/prover/casesplit"; //$NON-NLS-1$
	private static final String PROVER_SIMPLIFICATION_TRACE = PLUGIN_ID + "/debug/prover/simplification"; //$NON-NLS-1$
	private static final String PROVER_DUMPING_TRACE = PLUGIN_ID + "/debug/prover/dumping"; //$NON-NLS-1$
	private static final String PROVER_SEEDSEARCH_TRACE = PLUGIN_ID + "/debug/prover/seedsearch"; //$NON-NLS-1$
	
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
		if (isDebugging()) {
			String option;
//			option = Platform.getDebugOption(LOADER_TRACE);
//			if (option != null)
//				PredicateBuilder.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(LOADER_PHASE1_TRACE);
			if (option != null)
				AbstractContext.setDebugFlag(option.equalsIgnoreCase("true")); //$NON-NLS-1$
			option = Platform.getDebugOption(lOADER_PHASE2_TRACE);
			if (option != null)
				ClauseBuilder.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(PROVER_TRACE);
			if (option != null)
				PPProof.DEBUG = option.equalsIgnoreCase("true");
			option = Platform.getDebugOption(PROVER_STRATEGY_TRACE);
			if (option != null)
				ClauseDispatcher.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(PROVER_INFERENCE_TRACE);
			if (option != null) {
				PredicateProver.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(PROVER_CASESPLIT_TRACE);
			if (option != null)
				CaseSplitter.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(PROVER_DUMPING_TRACE);
			if (option != null)
				Dumper.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(PROVER_SIMPLIFICATION_TRACE);
			if (option != null)
				ClauseSimplifier.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(PROVER_SEEDSEARCH_TRACE);
			if (option != null)
				SeedSearchProver.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			
			}
		}
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
