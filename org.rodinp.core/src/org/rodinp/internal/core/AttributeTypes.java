/*******************************************************************************
 * Copyright (c) 2005, 2023 ETH Zurich and others.
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

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.rodinp.internal.core.ElementTypeManager.debug;
import static org.rodinp.internal.core.ElementTypeManager.getSortedIds;

import java.util.Collection;
import java.util.Set;

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
	
	private Set<AttributeType<?>> ubiquitousAttributeTypes;

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
			debug("    elements: [" + stream(type.getElementTypes()).map(e -> e.getId()).collect(joining(", ")) + "]");
		}
		debug("--------------------------------------------");
	}

	public void finalizeRelations() {
		for (final AttributeType<?> attrType : map.values()) {
			attrType.finalizeRelations();
		}
	}

	public void setUbiquitousAttributeTypes(
			Set<AttributeType<?>> ubiquitousAttributeTypes) {
		this.ubiquitousAttributeTypes = ubiquitousAttributeTypes;
	}

	public boolean isUbiquitous(AttributeType<?> attributeType) {
		return ubiquitousAttributeTypes.contains(attributeType);
	}

	public Collection<? extends AttributeType<?>> getUbiquitousAttributeTypes() {
		return ubiquitousAttributeTypes;
	}

}
