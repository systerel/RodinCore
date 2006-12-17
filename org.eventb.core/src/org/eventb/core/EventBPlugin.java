/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.internal.core.pm.UserSupportManager;
import org.eventb.internal.core.pog.ProofObligationGenerator;
import org.eventb.internal.core.pom.AutoPOM;
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
	private static final String POG_TRACE = PLUGIN_ID + "/debug/pog"; //$NON-NLS-1$
	private static final String POM_TRACE = PLUGIN_ID + "/debug/pom"; //$NON-NLS-1$
	
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
		
		configureDebugOptions();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
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
			option = Platform.getDebugOption(POG_TRACE);
			if (option != null)
				ProofObligationGenerator.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			option = Platform.getDebugOption(POM_TRACE);
			if (option != null)
				AutoPOM.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
		}
	}

	public IUserSupportManager getUserSupportManager() {
		return UserSupportManager.getDefault();
	}
}
