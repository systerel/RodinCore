/*******************************************************************************
 * Copyright (c) 2005,2008 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.pog.POGModule;
import org.eventb.core.sc.SCModule;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.internal.core.PSWrapper;
import org.eventb.internal.core.pm.PostTacticPreference;
import org.eventb.internal.core.pm.UserSupportManager;
import org.eventb.internal.core.pm.UserSupportUtils;
import org.eventb.internal.core.pog.ProofObligationGenerator;
import org.eventb.internal.core.pog.modules.UtilityModule;
import org.eventb.internal.core.pom.AutoPOM;
import org.eventb.internal.core.pom.POMTacticPreference;
import org.eventb.internal.core.pom.POLoader;
import org.eventb.internal.core.sc.StaticChecker;
import org.osgi.framework.BundleContext;

/**
 * The Event-B core plugin class.
 */
public class EventBPlugin extends Plugin {

	//The shared instance.
	private static EventBPlugin plugin;

	/**
	 * The plug-in identifier of the Event-B core support (value
	 * <code>"org.eventb.core"</code>).
	 */
	public static final String PLUGIN_ID = "org.eventb.core"; //$NON-NLS-1$
	
	/**
	 * debugging/tracing option names
	 */
	private static final String SC_TRACE = PLUGIN_ID + "/debug/sc"; //$NON-NLS-1$
	private static final String SC_TRACE_STATE = PLUGIN_ID + "/debug/sc/state"; //$NON-NLS-1$
	private static final String SC_TRACE_MODULECONF = PLUGIN_ID + "/debug/sc/moduleconf"; //$NON-NLS-1$
	private static final String SC_TRACE_MODULES = PLUGIN_ID + "/debug/sc/modules"; //$NON-NLS-1$
	private static final String SC_TRACE_MARKERS = PLUGIN_ID + "/debug/sc/markers"; //$NON-NLS-1$
	private static final String POG_TRACE = PLUGIN_ID + "/debug/pog"; //$NON-NLS-1$
	private static final String POG_TRACE_STATE = PLUGIN_ID + "/debug/pog/state"; //$NON-NLS-1$
	private static final String POG_TRACE_MODULECONF = PLUGIN_ID + "/debug/pog/moduleconf"; //$NON-NLS-1$
	private static final String POG_TRACE_MODULES = PLUGIN_ID + "/debug/pog/modules"; //$NON-NLS-1$
	private static final String POG_TRACE_TRIVIAL = PLUGIN_ID + "/debug/pog/trivial"; //$NON-NLS-1$
	private static final String POM_TRACE = PLUGIN_ID + "/debug/pom"; //$NON-NLS-1$
	private static final String PO_LOADER_TRACE = PLUGIN_ID + "/debug/poloader"; //$NON-NLS-1$
	private static final String PM_TRACE = PLUGIN_ID + "/debug/pm"; //$NON-NLS-1$
	private static final String PERF_POM_PROOFREUSE_TRACE = PLUGIN_ID + "/perf/pom/proofReuse"; //$NON-NLS-1$
	
	/**
	 * Returns the name of the component whose data are stored in the file with the given name.
	 * 
	 * @param fileName
	 *            name of the file
	 * @return the name of the component corresponding to the given file
	 */
	public static String getComponentName(String fileName) {
		int lastDot = fileName.lastIndexOf('.');
		if (lastDot == -1) {
			return fileName;
		} else {
			return fileName.substring(0, lastDot);
		}
	}

	/**
	 * Returns the name of the Rodin file that contains the context with the
	 * given name.
	 * 
	 * @param bareName
	 *            name of the context
	 * @return the name of the file containing that context
	 */
	public static String getContextFileName(String bareName) {
		return bareName + ".buc";
	}

	/**
	 * Returns the shared instance.
	 */
	public static EventBPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the name of the Rodin file that contains the machine with the
	 * given name.
	 * 
	 * @param bareName
	 *            name of the machine
	 * @return the name of the file containing that machine
	 */
	public static String getMachineFileName(String bareName) {
		return bareName + ".bum";
	}

	/**
	 * Returns the shared instance.
	 */
	public static EventBPlugin getPlugin() {
		return plugin;
	}
	
	/**
	 * Returns the name of the Rodin file that contains the proof obligations for the component of the
	 * given name.
	 * 
	 * @param bareName
	 *            name of the component
	 * @return the name of the file containing POs for that component
	 */
	public static String getPOFileName(String bareName) {
		return bareName + ".bpo";
	}

	/**
	 * Returns the name of the Rodin file that contains the proofs for the component of the
	 * given name.
	 * 
	 * @param bareName
	 *            name of the component
	 * @return the name of the file containing proofs for that component
	 */
	public static String getPRFileName(String bareName) {
		return bareName + ".bpr";
	}
	
	/**
	 * Returns the name of the Rodin file that contains the proof status 
	 * for the component of the given name.
	 * 
	 * @param bareName
	 *            name of the component
	 * @return the name of the file containing proofs for that component
	 */
	public static String getPSFileName(String bareName) {
		return bareName + ".bps";
	}

	/**
	 * Returns the name of the Rodin file that contains the checked context with the
	 * given name.
	 * 
	 * @param bareName
	 *            name of the checked context
	 * @return the name of the file containing that checked context
	 */
	public static String getSCContextFileName(String bareName) {
		return bareName + ".bcc";
	}

	/**
	 * Returns the name of the Rodin file that contains the checked machine with the
	 * given name.
	 * 
	 * @param bareName
	 *            name of the checked machine
	 * @return the name of the file containing that checked machine
	 */
	public static String getSCMachineFileName(String bareName) {
		return bareName + ".bcm";
	}

	/**
	 * Creates the Event-B core plug-in.
	 * <p>
	 * The plug-in instance is created automatically by the Eclipse platform.
	 * Clients must not call.
	 * </p>
	 */
	public EventBPlugin() {
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		enableAssertions();
		configureDebugOptions();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
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
			option = Platform.getDebugOption(SC_TRACE);
			if (option != null)
				StaticChecker.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(SC_TRACE_STATE);
			if (option != null)
				StaticChecker.DEBUG_STATE = 
					StaticChecker.DEBUG && option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(SC_TRACE_MODULECONF);
			if (option != null)
				StaticChecker.DEBUG_MODULECONF = 
					StaticChecker.DEBUG && option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(SC_TRACE_MODULES);
			if (option != null)
				SCModule.DEBUG_MODULE = 
					StaticChecker.DEBUG && option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(SC_TRACE_MARKERS);
			if (option != null)
				StaticChecker.DEBUG_MARKERS = 
					StaticChecker.DEBUG && option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(POG_TRACE);
			if (option != null)
				ProofObligationGenerator.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(POG_TRACE_STATE);
			if (option != null)
				ProofObligationGenerator.DEBUG_STATE = 
					ProofObligationGenerator.DEBUG && option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(POG_TRACE_MODULECONF);
			if (option != null)
				ProofObligationGenerator.DEBUG_MODULECONF = 
					ProofObligationGenerator.DEBUG && option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(POG_TRACE_MODULES);
			if (option != null)
				POGModule.DEBUG_MODULE = 
					ProofObligationGenerator.DEBUG && option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(POG_TRACE_TRIVIAL);
			if (option != null)
				UtilityModule.DEBUG_TRIVIAL = 
					ProofObligationGenerator.DEBUG && option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(POM_TRACE);
			if (option != null)
				AutoPOM.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(PO_LOADER_TRACE);
			if (option != null)
				POLoader.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(PM_TRACE);
			if (option != null)
				UserSupportUtils.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(PERF_POM_PROOFREUSE_TRACE);
			if (option != null)
				AutoPOM.PERF_PROOFREUSE = option.equalsIgnoreCase("true"); //$NON-NLS-1$
		}
	}

	/**
	 * Return the default user support manager.
	 * <p>
	 * 
	 * @return the default user support manager
	 * @author htson
	 */
	public IUserSupportManager getUserSupportManager() {
		return UserSupportManager.getDefault();
	}

	/**
	 * Return the post-tactic registry.
	 * <p>
	 * 
	 * @return the default post-tactic registry
	 * @deprecated use {@link #getPostTacticPreference()} 
	 * @author htson
	 */
	@Deprecated
	public org.eventb.core.pm.IPostTacticRegistry getPostTacticRegistry() {
		return org.eventb.internal.core.pm.PostTacticRegistry.getDefault();
	}

	/**
	 * Return the POM-tactic preference
	 * <p>
	 * 
	 * @return the default POM-tactic preference
	 * @author htson
	 */
	public static IAutoTacticPreference getPOMTacticPreference() {
		return POMTacticPreference.getDefault();
	}

	/**
	 * Return the post-tactic preference
	 * <p>
	 * 
	 * @return the default post-tactic preference
	 * @author htson
	 */
	public static IAutoTacticPreference getPostTacticPreference() {
		return PostTacticPreference.getDefault();
	}

	/**
	 * Returns an object encapsulating the proofs and proof statuses associated
	 * to the given event-B file.
	 * 
	 * @param file
	 *            an event-B file (machine, context, PO file, ...)
	 * @return an object encapsulating the PR and PS files associated to the
	 *         given file.
	 */
	public static IPSWrapper getPSWrapper(IEventBFile file) {
		return new PSWrapper(file.getPSFile());
	}
	
}
