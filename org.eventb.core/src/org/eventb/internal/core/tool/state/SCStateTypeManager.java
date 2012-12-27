/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.tool.state;



/**
 * @author Stefan Hallerstede
 *
 */
public class SCStateTypeManager extends StateTypeManager {

	/**
	 * The singleton manager
	 */
	private static final SCStateTypeManager MANAGER = new SCStateTypeManager();
	
	/**
	 * Returns the singleton ElementTypeManager
	 */
	public final static SCStateTypeManager getInstance() {
		return MANAGER;
	}
	
	private SCStateTypeManager() {
		super("scStateTypes");
	}

}
