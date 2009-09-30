/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCContext;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IStateType;

/**
 * State component that contains the closure of all contexts, extended or seen.
 * This state component contains all contexts that need to be there. 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface IContextTable extends ISCState {

	final static IStateType<IContextTable> STATE_TYPE = 
		SCCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".contextTable");
	
	/**
	 * Add a context of the closure
	 * 
	 * @param name the name of the context
	 * @throws CoreException if the context table is immutable
	 */
	void addContext(String name, ISCContext context) throws CoreException;
	
	/**
	 * Returns whether this closure contains the context with the specified element name.
	 * 
	 * @param name the element name of the context
	 * @return whether this closure contains the context with the specified element name
	 */
	boolean containsContext(String name);
	
	/**
	 * Returns the context with the specified element name, or <code>null</code> if it is
	 * not stored in this closure.
	 * 
	 * @param name the element name of the context
	 * @return the context with the specified element name, or <code>null</code> if it is
	 * not stored in this closure
	 */
	ISCContext getContext(String name);
	
	/**
	 * Returns the size of this context table.
	 * 
	 * @return the size of this context table
	 */
	int size();
}
