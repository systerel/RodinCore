/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.pog.POGCore;
import org.eventb.core.tool.IStateType;

/**
 * Basic information on the currently treated machine.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @author Stefan Hallerstede
 *
 */
public interface IMachineInfo extends IPOGState {

	final static IStateType<IAbstractEventActionTable> STATE_TYPE = 
		POGCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".machineInfo");
	
	/**
	 * Returns the statically checked abstract machine, or <code>null</code> if there is none.
	 * 
	 * @return the statically checked abstract machine, or <code>null</code> if there is none
	 */
	ISCMachineFile getAbstractMachine();
	
	/**
	 * Returns whether the machine managed by this manager is initial, i.e., it does not refine
	 * another machine. 
	 * <code>isInitialMachine()</code> is the same as <code>getAbstractMachine() == null</code>.
	 * 
	 * @return whether the machine managed by this manager is initial
	 */
	boolean isInitialMachine();

}
