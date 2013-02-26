/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.relations;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.rodinp.core.RodinCore;
import org.rodinp.internal.core.AttributeTypes;
import org.rodinp.internal.core.InternalElementTypes;

/**
 * Manages the relations when element types and attributes are loaded. This
 * class is responsible of reading itemRelations extensions, manage their
 * parsing using the {@link ItemRelationParser}, and setting them into element
 * and attribute types using the {@link RelationsComputer}.
 * 
 * @author Thomas Muller
 */
public class ItemRelations {

	private final InternalElementTypes elementTypes;
	private final AttributeTypes attributeTypes;

	// Local id of the itemRelations extension point of this plug-in
	protected static final String ITEM_RELATIONS_ID = "itemRelations";

	public ItemRelations(InternalElementTypes elementTypes,
			AttributeTypes attributeTypes) {
		this.elementTypes = elementTypes;
		this.attributeTypes = attributeTypes;
		setRelations();
		makeRelationsImmutable();
	}

	private void makeRelationsImmutable() {
		elementTypes.makeRelationsImmutable();
		attributeTypes.makeRelationsImmutable();
	}

	private void setRelations() {
		final IConfigurationElement[] relationConfigElements = readExtensions();
		final ItemRelationParser relParser = new ItemRelationParser(
				elementTypes, attributeTypes);
		relParser.parse(relationConfigElements);
		final RelationsComputer relComputer = new RelationsComputer();
		relComputer.setRelations(relParser.getRelations());
	}

	private IConfigurationElement[] readExtensions() {
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		return registry.getConfigurationElementsFor(RodinCore.PLUGIN_ID,
				ITEM_RELATIONS_ID);
	}

}
