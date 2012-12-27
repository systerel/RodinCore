/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.pog.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.pog.POGCore;
import org.eventb.core.tool.IStateType;
import org.rodinp.core.IRodinFile;

/**
 * Basic information on the currently treated machine.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface IMachineInfo extends IPOGState {

	final static IStateType<IAbstractEventActionTable> STATE_TYPE = 
		POGCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".machineInfo");
	
	/**
	 * Returns the statically checked abstract machine, or <code>null</code> if there is none.
	 * 
	 * @return the statically checked abstract machine, or <code>null</code> if there is none
	 */
	IRodinFile getAbstractMachine();
	
	/**
	 * Returns whether the machine managed by this manager is initial, i.e., it does not refine
	 * another machine. 
	 * <code>isInitialMachine()</code> is the same as <code>getAbstractMachine() == null</code>.
	 * 
	 * @return whether the machine managed by this manager is initial
	 */
	boolean isInitialMachine();

}
