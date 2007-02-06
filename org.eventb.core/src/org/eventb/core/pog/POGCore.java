/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog;

import org.eventb.core.tool.state.IToolState;
import org.eventb.core.tool.state.IToolStateType;
import org.eventb.internal.core.pog.StateTypeManager;

/**
 * @author Stefan Hallerstede
 *
 */
public final class POGCore {
	
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
	public static <T extends IToolState> IToolStateType<T> getToolStateType(
			String id) {
		final StateTypeManager manager = StateTypeManager.getInstance();
		final IToolStateType result = manager.getToolStateType(id);
		if (result != null) {
			return result;
		}
		throw new IllegalArgumentException("Unknown pog tool state type: " + id);
	}

}
