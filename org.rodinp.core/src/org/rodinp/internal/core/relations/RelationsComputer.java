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

import org.rodinp.core.IInternalElementType;
import org.rodinp.internal.core.relations.Relations.AttributeTypesRelations;
import org.rodinp.internal.core.relations.Relations.ElementTypesRelations;
import org.rodinp.internal.core.relations.api.IInternalElementType2;

/**
 * Provides a protocol to retrieve element relations.
 * 
 * @author Thomas Muller
 */
public class RelationsComputer {

	private final ElementTypesRelations elemRels;
	private final AttributeTypesRelations attrRels;

	public RelationsComputer() {
		elemRels = new ElementTypesRelations();
		attrRels = new AttributeTypesRelations();
	}

	public void computeRelations(List<ItemRelation> relations) {
		for (ItemRelation rel : relations) {
			final IInternalElementType<?> parentType = rel.getParentType();
			elemRels.putAll(parentType, rel.getChildTypes());
			attrRels.putAll(parentType, rel.getAttributeTypes());
		}
	}

	// public for testing purpose only
	public ElementTypesRelations getElementRelations() {
		return elemRels;
	}

	public void setRelations(IInternalElementType2<?> element) {
		final InternalElementType2<?> e2 = (InternalElementType2<?>) element;
		e2.setRelation(//
				elemRels.getParentTypes(element), //
				elemRels.getChildTypes(element), //
				attrRels.getAttributes(element) //
		);
	}
	
}
