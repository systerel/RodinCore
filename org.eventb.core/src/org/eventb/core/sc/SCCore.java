/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc;

import org.eventb.core.tool.state.IState;
import org.eventb.core.tool.state.IStateType;
import org.eventb.internal.core.tool.state.SCStateTypeManager;

/**
 * @author Stefan Hallerstede
 *
 */
public final class SCCore {
	
	/**
	 * Returns the tool state type with the given id.
	 * 
	 * @param id
	 *            unique identifier of the tool state type
	 * @return the tool state type with the given id
	 * @throws IllegalArgumentException
	 *             if no such tool state type has been contributed
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IState> IStateType<T> getToolStateType(
			String id) {
		final SCStateTypeManager manager = SCStateTypeManager.getInstance();
		final IStateType result = manager.getStateType(id);
		if (result != null) {
			return result;
		}
		throw new IllegalArgumentException("Unknown sc tool state type: " + id);
	}

}
