/*******************************************************************************
 * Copyright (c) 2008 Systerel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.ast;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class ASTPlugin extends Plugin {

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		enableAssertions();
	}

	/**
	 * Enables Java assertion checks for this plug-in.
	 */
	private void enableAssertions() {
		getClass().getClassLoader().setDefaultAssertionStatus(true);
	}

}
