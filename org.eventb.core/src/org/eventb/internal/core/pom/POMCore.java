/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.pom;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.IPRFile;

/**
 * @author halstefa
 *
 */
public class POMCore {

	public static final String AUTO_POM_TOOL_ID = EventBPlugin.PLUGIN_ID + ".autoPOM"; //$NON-NLS-1$
	
	public static void runAutoPOG(IPOFile poFile, IPRFile prFile) throws CoreException {
		AutoPOM pom = new AutoPOM();
		pom.init(poFile, prFile, null, null);
		pom.writePRFile();
	}

}
