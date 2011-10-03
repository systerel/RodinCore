package org.rodinp.core.emf.tests;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class RodinEmfTestPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.rodinp.core.emf.tests"; //$NON-NLS-1$

	// The shared instance
	private static RodinEmfTestPlugin plugin;
	
	/**
	 * The constructor
	 */
	public RodinEmfTestPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static RodinEmfTestPlugin getDefault() {
		return plugin;
	}

}
