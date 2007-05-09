package org.eventb.internal.pp.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eventb.internal.pp.core.provers.casesplit.CaseSplitter;
import org.eventb.internal.pp.core.provers.predicate.PredicateProver;
import org.eventb.internal.pp.core.provers.seedsearch.SeedSearchProver;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.predicate.PredicateBuilder;
import org.eventb.pp.PPProof;
import org.osgi.framework.BundleContext;

public class ProverPlugin extends Plugin {

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
	
	public ProverPlugin() {
		// TODO Auto-generated constructor stub
	}
	
//	/**
//	 * The shared instance.
//	 */
//	private static ProverPlugin plugin;
	
	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
//		plugin = this;
		
		configureDebugOptions();
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
				PredicateBuilder.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(lOADER_PHASE2_TRACE);
			if (option != null)
				ClauseBuilder.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(PROVER_TRACE);
			if (option != null)
				PPProof.DEBUG = option.equalsIgnoreCase("true");
			option = Platform.getDebugOption(PROVER_STRATEGY_TRACE);
			if (option != null)
				ProofStrategy.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
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
//		plugin = null;
	}
}
