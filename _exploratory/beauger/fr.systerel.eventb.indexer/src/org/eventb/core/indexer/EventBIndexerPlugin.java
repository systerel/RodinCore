/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.indexer;

import org.eclipse.core.runtime.Plugin;
import org.eventb.core.IContextFile;
import org.osgi.framework.BundleContext;
import org.rodinp.core.index.RodinIndexer;


public class EventBIndexerPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "fr.systerel.eventb.indexer";

	// The shared instance
	private static EventBIndexerPlugin plugin;
	
	/**
	 * The constructor
	 */
	public EventBIndexerPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
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
	public static EventBIndexerPlugin getDefault() {
		return plugin;
	}

}
