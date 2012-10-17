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

import static java.util.Collections.unmodifiableSet;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

	private final Set<InternalElementType2<?>> elemTypes;
	private final Set<AttributeType<?>> attrTypes;

	public RelationsComputer() {
		elemRels = new ElementTypeRelations();
		attrRels = new AttributeTypeRelations();
		elemTypes = new LinkedHashSet<InternalElementType2<?>>();
		attrTypes = new LinkedHashSet<AttributeType<?>>();
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
			final InternalElementType2<?> parentType = rel.getParentType();
			final List<InternalElementType2<?>> childTypes = rel
					.getChildTypes();
			elemRels.putAll(parentType, childTypes);
			elemTypes.add(parentType);
			elemTypes.addAll(childTypes);
			final List<AttributeType<?>> attributeTypes = rel
					.getAttributeTypes();
			attrRels.putAll(parentType, attributeTypes);
			attrTypes.addAll(attributeTypes);
		}
	}

	/**
	 * Sets the relationship information of all element types concerned by the
	 * computed relations.
	 * 
	 * @throws IllegalStateException
	 *             if the relationship information of the given element type has
	 *             already been set
	 */
	public void setElementRelations() {
		for (InternalElementType2<?> type : elemTypes) {
			type.setRelation(//
					elemRels.getParentTypes(type), //
					elemRels.getChildTypes(type), //
					attrRels.getAttributes(type) //
			);
		}
	}

	/**
	 * Sets the relationship information of all element types concerned byg the
	 * computed relations.
	 * 
	 * @throws IllegalStateException
	 *             if the relationship information of the given attribute type
	 *             has already been set
	 */
	public void setAttributeRelations() {
		for (AttributeType<?> attribute : attrTypes) {
			attribute.setRelation(attrRels.getElementsTypes(attribute));
		}
	}

	public Set<InternalElementType2<?>> getElemTypes() {
		return unmodifiableSet(elemTypes);
	}

	public Set<AttributeType<?>> getAttributeTypes() {
		return unmodifiableSet(attrTypes);
	}

}
