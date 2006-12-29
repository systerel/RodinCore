/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;

import java.util.List;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.FreeIdentifier;

/**
 * Protocol for accessing all variables of a machine.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IMachineVariableTable extends IState, Iterable<FreeIdentifier> {

	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".machineVariableTable";

	/**
	 * Returns whether this variable table contains the specified variable.
	 * 
	 * @param variable the variable whose presence is to be tested
	 * @return whether this variable table contains the specified variable
	 */
	boolean contains(FreeIdentifier variable);
	
	/**
	 * Returns the array of variables that were already present in 
	 * the abstraction of this machine.
	 * 
	 * @return the array of variables that were already present in 
	 * the abstraction of this machine
	 */
	List<FreeIdentifier> getPreservedVariables();
			
}
