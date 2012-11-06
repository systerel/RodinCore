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
import org.rodinp.internal.core.relations.tomerge.InternalElementType2;

/**
 * Computes the item relations that have been parsed in order to provide
 * internal element types and attribute types with relationship information.
 * 
 * @author Thomas Muller
 * @see InternalElementType2
 * @see AttributeType
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
	 * Fills item types involved in the given item relations in with relation
	 * information.
	 * 
	 * @param relations
	 *            the item relation that have been parsed and will be filled in
	 *            the manipulated element types
	 */
	public void setRelations(List<ItemRelation> relations) {
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
		setElementRelations();
		setAttributeRelations();
	}

	private void setElementRelations() {
		for (InternalElementType2<?> type : elemTypes) {
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

	public Set<InternalElementType2<?>> getElemTypes() {
		return unmodifiableSet(elemTypes);
	}

	public Set<AttributeType<?>> getAttributeTypes() {
		return unmodifiableSet(attrTypes);
	}

}
