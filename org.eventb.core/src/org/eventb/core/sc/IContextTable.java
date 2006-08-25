/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCContext;

/**
 * State component that serves to verify that the closure of the seen contexts of 
 * an abstract machine is contained in the closure of the seen contexts of the concrete
 * machine. 
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IContextTable extends IState {

	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".contextTable";
	
	/**
	 * Add a context of the closure of the abstract machine
	 * 
	 * @param name the name of the context
	 */
	void addContext(String name, ISCContext context);
	
	boolean containsContext(String name);
	
	ISCContext getContext(String name);
	
	int size();
}
