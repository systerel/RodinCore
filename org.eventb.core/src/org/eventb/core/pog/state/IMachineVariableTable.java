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
 * @author Stefan Hallerstede
 *
 */
public interface IMachineVariableTable extends IPOGState, Iterable<FreeIdentifier> {

	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".machineVariableTable";

	boolean contains(FreeIdentifier variable);
	
	void add(FreeIdentifier variable, boolean preserved);
	
	List<FreeIdentifier> getPreservedVariables();
	
	void trimToSize();
		
}
