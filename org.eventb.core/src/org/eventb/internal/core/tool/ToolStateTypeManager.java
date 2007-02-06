/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.EventBPlugin;
import org.eventb.core.tool.state.IToolState;

/**
 * @author Stefan Hallerstede
 *
 */
public class ToolStateTypeManager {
	
	// Debug flag set from tracing options 
	public static boolean VERBOSE = false;

	// Local id of the fileElementTypes extension point of this plugin
	private final String TOOL_STATE_TYPES_ID;
	
	// Access to internal element types using their unique id
	private HashMap<String, ToolStateType<? extends IToolState>>
		toolStateTypeIds;

	private void computeToolStateTypes() {
		toolStateTypeIds =
			new HashMap<String, ToolStateType<? extends IToolState>>();
		
		// Read the extension point extensions.
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = 
			registry.getConfigurationElementsFor(EventBPlugin.PLUGIN_ID, TOOL_STATE_TYPES_ID);
		
		for (IConfigurationElement element: elements) {
			ToolStateType<? extends IToolState> type =
				new ToolStateType<IToolState>(element);
			toolStateTypeIds.put(type.getId(), type);
		}

		if (VERBOSE) {
			System.out.println("---------------------------------------------------");
			System.out.println(TOOL_STATE_TYPES_ID + " registered:");
			for (String id: getSortedIds(toolStateTypeIds)) {
				ToolStateType type = toolStateTypeIds.get(id);
				System.out.println("  " + type.getId());
				System.out.println("    name: " + type.getName());
				System.out.println("    class: " + type.getClassName());
			}
			System.out.println("---------------------------------------------------");
		}
	}

	private <V> String[] getSortedIds(HashMap<String, V> map) {
		Set<String> idSet = map.keySet();
		String[] ids = idSet.toArray(new String[idSet.size()]);
		Arrays.sort(ids);
		return ids;
	}
	
	/**
	 * Returns the internal element type with the given id.
	 * 
	 * @param id
	 *            the id of the element type to retrieve
	 * @return the element type or <code>null</code> if this
	 *         element type id is unknown.
	 */
	public ToolStateType<? extends IToolState> getToolStateType(
			String id) {

		if (toolStateTypeIds == null) {
			computeToolStateTypes();
		}
		return toolStateTypeIds.get(id);
	}

	protected ToolStateTypeManager(final String tool_state_types_id) {
		TOOL_STATE_TYPES_ID = tool_state_types_id;
	}
	

}
