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
import org.rodinp.internal.core.AttributeType;
import org.rodinp.internal.core.relations.Relations.AttributeTypeRelations;
import org.rodinp.internal.core.relations.Relations.ElementTypeRelations;
import org.rodinp.internal.core.relations.api.IAttributeType2;
import org.rodinp.internal.core.relations.api.IInternalElementType2;
import org.rodinp.internal.core.relations.tomerge.InternalElementType2;

/**
 * Computes the item relations that have been parsed in order to provide
 * internal element types and attribute types with relationship information.
 * 
 * @author Thomas Muller
 * @see IInternalElementType2
 * @see IAttributeType2
 * @see ItemRelation
 */
public class RelationsComputer {

	private final ElementTypeRelations elemRels;
	private final AttributeTypeRelations attrRels;

	public RelationsComputer() {
		elemRels = new ElementTypeRelations();
		attrRels = new AttributeTypeRelations();
	}

	/**
	 * Builds a convenient representation of the item relations to further
	 * provide internal element types and attribute types with relationship
	 * information.
	 * 
	 * @param relations
	 *            the item relation that have been parsed
	 */
	public void computeRelations(List<ItemRelation> relations) {
		for (ItemRelation rel : relations) {
			final IInternalElementType<?> parentType = rel.getParentType();
			elemRels.putAll(parentType, rel.getChildTypes());
			attrRels.putAll(parentType, rel.getAttributeTypes());
		}
	}

	/**
	 * Sets the relationship information of the given internal element type.
	 * 
	 * @param element
	 *            a given element type
	 * @throws IllegalAccessError
	 *             if the relationship information of the given element type has
	 *             already been set
	 */
	public void setElementRelations(IInternalElementType<?> element) {
		final InternalElementType2<?> e2 = (InternalElementType2<?>) element;
		e2.setRelation(//
				elemRels.getParentTypes(element), //
				elemRels.getChildTypes(element), //
				attrRels.getAttributes(element) //
		);
	}

	/**
	 * Sets the relationship information of the given attibute type.
	 * 
	 * @param attribute
	 *            a given attribute type
	 * @throws IllegalAccessError
	 *             if the relationship information of the given attribute type
	 *             has already been set
	 */
	public void setAttributeRelations(IAttributeType attribute) {
		final AttributeType<?> a2 = (AttributeType<?>) attribute;
		a2.setRelation(attrRels.getElementsTypes(attribute));
	}

}
