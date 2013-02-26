/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - code extracted from class ElementTypeManager
 *******************************************************************************/
package org.rodinp.internal.core;

import static org.rodinp.internal.core.ElementTypeManager.debug;
import static org.rodinp.internal.core.ElementTypeManager.getSortedIds;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IAttributeType;

/**
 * Stores a map between ids and attribute types, as defined by the extension
 * point <code>attributeTypes</code>.
 * <p>
 * Instances of this class are immutable and therefore thread-safe.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class AttributeTypes extends ContributedItemTypes<AttributeType<?>> {

	// Local id of the attributeTypes extension point of this plug-in
	private static final String ATTRIBUTE_TYPES_ID = "attributeTypes";


	public AttributeTypes(ElementTypeManager elementTypeManager) {
		super(ATTRIBUTE_TYPES_ID, elementTypeManager);
	}

	@Override
	protected AttributeType<?> makeType(IConfigurationElement element) {
		return AttributeType.valueOf(element);
	}

	@Override
	protected void showMap() {
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

	public void makeRelationsImmutable() {
		for (AttributeType<?> attrType : map.values()) {
			attrType.makeRelationsImmutable();
		}
	}

}
