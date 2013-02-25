/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
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
import org.rodinp.internal.core.InternalElementType;
import org.rodinp.internal.core.relations.Relations.AttributeTypeRelations;
import org.rodinp.internal.core.relations.Relations.ElementTypeRelations;

/**
 * Computes the item relations that have been parsed in order to provide
 * internal element types and attribute types with relationship information.
 * 
 * @author Thomas Muller
 * @see InternalElementType
 * @see AttributeType
 * @see ItemRelation
 */
public class RelationsComputer {

	private final ElementTypeRelations elemRels;
	private final AttributeTypeRelations attrRels;

	private final Set<InternalElementType<?>> elemTypes;
	private final Set<AttributeType<?>> attrTypes;

	public RelationsComputer() {
		elemRels = new ElementTypeRelations();
		attrRels = new AttributeTypeRelations();
		elemTypes = new LinkedHashSet<InternalElementType<?>>();
		attrTypes = new LinkedHashSet<AttributeType<?>>();
	}

	/**
	 * Fills item types involved in the given item relations in with relation
	 * information.
	 * 
	 * @param relations
	 *            the item relation that have been parsed and will be filled in
	 *            the manipulated element types
	 */
	public void setRelations(List<ItemRelation> relations) {
		for (ItemRelation rel : relations) {
			final InternalElementType<?> parentType = rel.getParentType();
			final List<InternalElementType<?>> childTypes = rel
					.getChildTypes();
			elemRels.putAll(parentType, childTypes);
			elemTypes.add(parentType);
			elemTypes.addAll(childTypes);
			final List<AttributeType<?>> attributeTypes = rel
					.getAttributeTypes();
			attrRels.putAll(parentType, attributeTypes);
			attrTypes.addAll(attributeTypes);
		}
		setElementRelations();
		setAttributeRelations();
	}

	private void setElementRelations() {
		for (InternalElementType<?> type : elemTypes) {
			type.setRelation(//
					elemRels.getParentTypes(type), //
					elemRels.getChildTypes(type), //
					attrRels.getAttributeTypes(type) //
			);
		}
	}

	public void setAttributeRelations() {
		for (AttributeType<?> attribute : attrTypes) {
			attribute.setRelation(attrRels.getElementTypes(attribute));
		}
	}

	// For testing purposes
	public Set<InternalElementType<?>> getElemTypes() {
		return unmodifiableSet(elemTypes);
	}

	// For testing purposes
	public Set<AttributeType<?>> getAttributeTypes() {
		return unmodifiableSet(attrTypes);
	}

}
