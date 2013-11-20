/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.seqprover.core.perf.app;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class AppPlugin extends Plugin {

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		Application.DEBUG = isDebugging();
	}

}
