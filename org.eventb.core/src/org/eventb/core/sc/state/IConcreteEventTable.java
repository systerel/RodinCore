/*******************************************************************************
 * Copyright (c) 2008 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IStateType;

/**
 * This state component stores a table with information about refinement relationships of concrete events.
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface IConcreteEventTable extends ISCState, Iterable<IConcreteEventInfo> {
	
	final static IStateType<IConcreteEventInfo> STATE_TYPE = 
		SCCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".concreteEventTable");

	IConcreteEventInfo getConcreteEventInfo(String symbol) throws CoreException;
	
	void addConcreteEventInfo(IConcreteEventInfo info) throws CoreException;
	
	
}
