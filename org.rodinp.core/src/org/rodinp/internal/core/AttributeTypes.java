/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - code extracted from method ElementTypeManager
 *******************************************************************************/
package org.rodinp.internal.core;

import static org.rodinp.internal.core.ItemTypeUtils.debug;
import static org.rodinp.internal.core.ItemTypeUtils.getSortedIds;

import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.RodinCore;

/**
 * 
 * /** Stores a map between ids and attribute types, as defined by the extension
 * point <code>attributeTypes</code>.
 * <p>
 * Instances of this class are immutable and therefore thread-safe.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class AttributeTypes {
	// Local id of the internalElementTypes extension point of this plug-in
	protected static final String ATTRIBUTE_TYPES_ID = "attributeTypes";

	private final HashMap<String, IAttributeType> map//
	= new HashMap<String, IAttributeType>();

	public AttributeTypes() {
			computeAttributeTypes();
	}
	
	public IAttributeType get(String id) {
		return map.get(id);
	}
	
	protected void computeAttributeTypes() {
		final IConfigurationElement[] elements = readExtensions();
		for (IConfigurationElement element: elements) {
			final AttributeType<?> attrType = AttributeType.valueOf(element);
			if (attrType != null) {
				map.put(attrType.getId(), attrType);
			}
		}

		if (ElementTypeManager.VERBOSE) {
			debug("--------------------------------------------");
			debug("Attribute types known to the Rodin database:");
			for (String id : getSortedIds(map)) {
				final IAttributeType type = map.get(id);
				debug("  " + type.getId());
				debug("    name:  " + type.getName());
				if (type instanceof AttributeType<?>) {
					debug("    kind:  " + ((AttributeType<?>) type).getKind());
				}
				debug("    class: " + type.getClass());
			}
			debug("--------------------------------------------");
		}
	}
	
	protected IConfigurationElement[] readExtensions() {
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		return registry.getConfigurationElementsFor(RodinCore.PLUGIN_ID,
				ATTRIBUTE_TYPES_ID);
	}
	
}
