/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.protopog;

import org.eventb.core.EventBPlugin;

/**
 * @author halstefa
 *
 */
public class POGCore {

	public static final String CONTEXT_POG_TOOL_ID = EventBPlugin.PLUGIN_ID + ".contextPOG"; //$NON-NLS-1$
	
	public static final String MACHINE_POG_TOOL_ID = EventBPlugin.PLUGIN_ID + ".machinePOG"; //$NON-NLS-1$

//	public static void runContextPOG(ISCContext context, IPOFile poFile) throws CoreException {
//		ContextPOG pog = new ContextPOG();
//		pog.init(context, poFile, null, null);
//		pog.run();
//	}
//
//	public static void runMachinePOG(ISCMachine machine, IPOFile poFile) throws CoreException {
//		MachinePOG pog = new MachinePOG();
//		pog.init(machine, poFile, null, null);
//		pog.run();
//	}

}
