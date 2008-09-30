package org.eventb.core.indexer;

import org.eclipse.core.runtime.Plugin;
import org.eventb.core.IContextFile;
import org.osgi.framework.BundleContext;
import org.rodinp.core.index.RodinIndexer;


public class EventBIndexer extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "fr.systerel.eventb.indexer";

	// The shared instance
	private static EventBIndexer plugin;
	
	/**
	 * The constructor
	 */
	public EventBIndexer() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		System.out.println("Starting EventBIndexer");
		RodinIndexer.register(new ContextIndexer(), IContextFile.ELEMENT_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
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
	public static EventBIndexer getDefault() {
		return plugin;
	}

}
