/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.relations;

import java.util.List;

import org.rodinp.internal.core.InternalElementTypes;
import org.rodinp.internal.core.relations.Relations.AttributeRelations;
import org.rodinp.internal.core.relations.Relations.ElementRelations;

/**
 * Instantiate types in relations and provides a protocol to retrieve them.
 * 
 * @author Thomas Muller
 */
public class RelationsComputer {

	private final ElementRelations elementRelations;
	private final AttributeRelations attributeRelations;

	public RelationsComputer(InternalElementTypes types) {
		elementRelations = new ElementRelations(types);
		attributeRelations = new AttributeRelations(types);
	}

	public void computeRelations(List<ItemRelation> relations) {
		for (ItemRelation rel : relations) {
			final String parentId = rel.getParentTypeId();
			elementRelations.putAll(parentId, rel.getChildTypeIds());
			attributeRelations.putAll(parentId, rel.getAttributeTypeIds());
		}
	}

	// public for testing purpose only
	public ElementRelations getElementRelations() {
		return elementRelations;
	}
	
}
