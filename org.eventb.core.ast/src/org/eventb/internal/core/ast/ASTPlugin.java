/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class ASTPlugin extends Plugin {

	public static final String PLUGIN_ID = "org.eventb.core.ast"; //$NON-NLS-1$

	private static ASTPlugin plugin;
	
	public static ASTPlugin getPlugin() {
		return plugin;
	}
	
	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		enableAssertions();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}
	/**
	 * Enables Java assertion checks for this plug-in.
	 */
	private void enableAssertions() {
		getClass().getClassLoader().setDefaultAssertionStatus(true);
	}

	
	public static void log(Throwable exc, String message) {
		if (message == null) {
			message = "Unknown context"; //$NON-NLS-1$
		}
		final IStatus status = new Status(IStatus.ERROR, PLUGIN_ID,
				IStatus.ERROR, message, exc);
		getPlugin().getLog().log(status);
	}


}
