/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eventb.internal.core.tool.ToolStateTypeManager;

/**
 * @author Stefan Hallerstede
 *
 */
public class StateTypeManager extends ToolStateTypeManager {

	/**
	 * The singleton manager
	 */
	private static final StateTypeManager MANAGER = new StateTypeManager();
	
	/**
	 * Returns the singleton ElementTypeManager
	 */
	public final static StateTypeManager getInstance() {
		return MANAGER;
	}
	
	private StateTypeManager() {
		super("pogStateTypes");
	}

}
