/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.lightcore;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rodinp.core.emf.lightcore.adapters.DeltaProcessor;
import org.rodinp.core.emf.lightcore.adapters.dboperations.DeltaProcessManager;

/**
 * Activator for the RODIN-EMF core plug-in
 */
public class LightCorePlugin extends Plugin implements BundleActivator {

	public static final String PLUGIN_ID = "org.rodinp.core.emf";

	public static boolean DEBUG;
	
	// Tracing options
	private static final String MAIN_TRACE = PLUGIN_ID + "/debug"; //$NON-NLS-1$
	private static final String DELTAPROC_TRACE = PLUGIN_ID +"/debug/deltaprocessor"; //$NON-NLS-1$
	private static final String DELTAPROC_MGMT_TRACE = PLUGIN_ID +"/debug/deltaprocessormanager"; //$NON-NLS-1$
	
	// The shared instance
	private static LightCorePlugin PLUGIN;


	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		PLUGIN = this;
		if (isDebugging())
			configureDebugOptions();
		DeltaProcessManager.startDeltaProcess();
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		DeltaProcessManager.stopDeltaProcess();
		PLUGIN = null;
		super.stop(context);
	}
	
	public static LightCorePlugin getDefault() {
		return PLUGIN;
	}

	/**
	 * Process debugging/tracing options coming from Eclipse.
	 */
	private void configureDebugOptions() {
		DEBUG = parseOption(MAIN_TRACE);
		if (DEBUG)
		DeltaProcessor.DEBUG = parseOption(DELTAPROC_TRACE);
		DeltaProcessManager.DEBUG = parseOption(DELTAPROC_MGMT_TRACE);
	}

	private static boolean parseOption(String key) {
		final String option = Platform.getDebugOption(key);
		return "true".equalsIgnoreCase(option); //$NON-NLS-1$
	}

}
