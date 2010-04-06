/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool.state;



/**
 * @author Stefan Hallerstede
 *
 */
public class POGStateTypeManager extends StateTypeManager {

	/**
	 * The singleton manager
	 */
	private static final POGStateTypeManager MANAGER = new POGStateTypeManager();
	
	/**
	 * Returns the singleton ElementTypeManager
	 */
	public final static POGStateTypeManager getInstance() {
		return MANAGER;
	}
	
	private POGStateTypeManager() {
		super("pogStateTypes");
	}

}
