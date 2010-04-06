/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog;

import org.eventb.core.tool.IModuleType;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.POGModuleManager;
import org.eventb.internal.core.tool.state.POGStateTypeManager;
import org.eventb.internal.core.tool.types.IModule;
import org.eventb.internal.core.tool.types.IState;

/**
 * @author Stefan Hallerstede
 *
 * @since 1.0
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
	public static <T extends IState> IStateType<T> getToolStateType(
			final String id) {
		final POGStateTypeManager manager = POGStateTypeManager.getInstance();
		final IStateType result = manager.getStateType(id);
		if (result != null) {
			return result;
		}
		throw new IllegalArgumentException("Unknown POG tool state type: " + id);
	}

	/**
	 * Returns the module type with the given id.
	 * 
	 * @param id
	 *            unique identifier of the module type
	 * @return the module type with the given id
	 * @throws IllegalArgumentException
	 *             if no such module type has been contributed
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IModule> IModuleType<T> getModuleType(
			final String id) {
		final POGModuleManager manager = POGModuleManager.getInstance();
		final IModuleType result = manager.getModuleDesc(id);
		if (result != null) {
			return result;
		}
		throw new IllegalArgumentException("Unknown POG module type: " + id);
	}

}
