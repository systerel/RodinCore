/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.protosc;

import org.eventb.core.EventBPlugin;

/**
 * @author halstefa
 *
 */
public class SCCore {
	
	public static final String CONTEXT_SC_TOOL_ID = EventBPlugin.PLUGIN_ID + ".contextSC"; //$NON-NLS-1$
	
	public static final String MACHINE_SC_TOOL_ID = EventBPlugin.PLUGIN_ID + ".machineSC"; //$NON-NLS-1$
	
	public static final String MACHINE_SEES_REL_ID = EventBPlugin.PLUGIN_ID + ".machineSEES"; //$NON-NLS-1$

//	/**
//	 * This method is for testing purposes and can change without notice.
//	 * @param context
//	 * @param scContext
//	 * @throws CoreException
//	 */
//	public static void runContextSC(IContextFile context, ISCContextFile scContext) throws CoreException {
//		ContextSC staticChecker = new ContextSC();
//		staticChecker.init(context, scContext, null, null);
//		staticChecker.runSC();
//
//	}
//	
//	/**
//	 * This method is for testing purposes and can change without notice.
//	 * @param machine
//	 * @param scMachine
//	 * @throws CoreException
//	 */
//	public static void runMachineSC(IMachineFile machine, ISCMachineFile scMachine) throws CoreException {
//		MachineSC staticChecker = new MachineSC();
//		staticChecker.init(machine, scMachine, null, null);
//		staticChecker.runSC();
//
//	}

}
