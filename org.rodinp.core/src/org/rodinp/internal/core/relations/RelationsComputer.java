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

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;
import org.rodinp.internal.core.InternalElementTypes;
import org.rodinp.internal.core.relations.Relations.AttributeRelations;
import org.rodinp.internal.core.relations.Relations.ElementRelations;
import org.rodinp.internal.core.relations.api.IInternalElementType2;

/**
 * Instantiate types in relations and provides a protocol to retrieve them.
 * 
 * @author Thomas Muller
 */
public class RelationsComputer {

	private final ElementRelations elemRels;
	private final AttributeRelations attrRels;

	public RelationsComputer(InternalElementTypes types) {
		elemRels = new ElementRelations(types);
		attrRels = new AttributeRelations(types);
	}

	public void computeRelations(List<ItemRelation> relations) {
		for (ItemRelation rel : relations) {
			final String parentId = rel.getParentTypeId();
			elemRels.putAll(parentId, rel.getChildTypeIds());
			attrRels.putAll(parentId, rel.getAttributeTypeIds());
		}
	}

	// public for testing purpose only
	public ElementRelations getElementRelations() {
		return elemRels;
	}

	public void setRelations(IInternalElementType2<?> element) {
		final IInternalElementType<?>[] parentTypes = //
		elemRels.getParentTypes(element);
		final IInternalElementType<?>[] childTypes = //
		elemRels.getChildTypes(element);
		final IAttributeType[] attributeTypes = attrRels.getAttributes(element);
		final InternalElementType2<?> e2 = (InternalElementType2<?>) element;
		e2.setRelation(parentTypes, childTypes, attributeTypes);
	}
	
}
