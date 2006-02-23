package org.rodinp.platform;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class PlatformPlugin extends Plugin {

	//The shared instance.
	private static PlatformPlugin plugin;
	
	/**
	 * The plug-in identifier of the Rodin platform support (value
	 * <code>"org.rodinp.platform"</code>).
	 */
	public static final String PLUGIN_ID = "org.rodinp.platform"; //$NON-NLS-1$

	/**
	 * The part identifier of the Rodin platform intro (value
	 * <code>"org.rodinp.platform.intro"</code>).
	 */
	public static final String PLATFORM_INTRO_PART = PLUGIN_ID + "intro"; //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public PlatformPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
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
	public static PlatformPlugin getDefault() {
		return plugin;
	}

}
