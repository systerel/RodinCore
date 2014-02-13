/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.tool.state;

import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.EventBPlugin;
import org.eventb.core.tool.IState;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.tool.SortingUtil;
import org.eventb.internal.core.tool.BasicDesc.ModuleLoadingException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class StateTypeManager extends SortingUtil {
	
	// Debug flag set from tracing options 
	public static boolean VERBOSE = false;

	// Local id of the state types extension point of this plugin
	private final String state_types_id;
	
	// Access to state types using their unique id
	private HashMap<String, StateType<? extends IState>>
		stateTypeIds;

	private void register(String id, StateType<? extends IState> type) {
		final StateType<? extends IState> oldType = stateTypeIds.put(id, type);
		if (oldType != null) {
			stateTypeIds.put(id, oldType);
			throw new IllegalStateException(
					"Attempt to create twice state type " + id);
		}
	}
	
	private void computeStateTypes() {
		stateTypeIds =
			new HashMap<String, StateType<? extends IState>>();
		
		// Read the extension point extensions.
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = 
			registry.getConfigurationElementsFor(EventBPlugin.PLUGIN_ID, state_types_id);
		
		for (IConfigurationElement element: elements) {
			try {
				StateType<? extends IState> type = new StateType<IState>(element);
				register(type.getId(), type);
			} catch (ModuleLoadingException e) {
				Util.log(e.getCause(), " while computing state type " + element.getValue());
				// ignore module
			}
		}

		if (VERBOSE) {
			System.out.println("---------------------------------------------------");
			System.out.println(state_types_id + " registered:");
			for (String id: getSortedIds(stateTypeIds)) {
				StateType<?> type = stateTypeIds.get(id);
				System.out.println("  " + type.getId());
				System.out.println("    name: " + type.getName());
				System.out.println("    class: " + type.getClassName());
			}
			System.out.println("---------------------------------------------------");
		}
	}

	/**
	 * Returns the state type with the given id.
	 * 
	 * @param id
	 *            the id of the state type to retrieve
	 * @return the element type or <code>null</code> if this
	 *         state type id is unknown.
	 */
	public StateType<? extends IState> getStateType(
			String id) {

		if (stateTypeIds == null) {
			computeStateTypes();
		}
		return stateTypeIds.get(id);
	}

	protected StateTypeManager(final String state_types_id) {
		this.state_types_id = state_types_id;
	}
	
}
