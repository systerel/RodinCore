package org.eventb.core.seqprover;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eventb.core.seqprover.xprover.XProverReasoner;
import org.eventb.internal.core.seqprover.ProverChecks;
import org.eventb.internal.core.seqprover.ReasonerRegistry;
import org.eventb.internal.core.seqprover.AutoTacticRegistry;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractAutoRewrites;
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
	
	/**
	 * The shared instance.
	 */
	private static SequentProver plugin;

	/**
	 * Debug flag for <code>SEQPROVER_TRACE</code>
	 */
	private static boolean DEBUG;

	/**
	 * Creates the Sequent Prover plug-in.
	 * <p>
	 * The plug-in instance is created automatically by the Eclipse platform.
	 * Clients must not call.
	 * </p>
	 */
	public SequentProver() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		enableAssertions();
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
		if (isDebugging()) {
			String option;
			option = Platform.getDebugOption(SEQPROVER_TRACE);
			if (option != null)
				SequentProver.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(PROVER_CHECKS_TRACE);
			if (option != null)
				ProverChecks.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(REASONER_REGISTRY_TRACE);
			if (option != null)
				ReasonerRegistry.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(TACTIC_REGISTRY_TRACE);
			if (option != null)
				AutoTacticRegistry.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(XPROVER_TRACE);
			if (option != null)
				XProverReasoner.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(AUTO_REWRITER_TRACE);
			if (option != null)
				AbstractAutoRewrites.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
		}
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
