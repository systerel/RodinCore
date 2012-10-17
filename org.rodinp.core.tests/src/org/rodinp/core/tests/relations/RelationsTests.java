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
package org.rodinp.core.tests.relations;

import static java.util.regex.Pattern.compile;
import static org.junit.Assert.assertArrayEquals;
import static org.rodinp.core.tests.relations.ItemRelationParserTests.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;
import org.rodinp.internal.core.AttributeType;
import org.rodinp.internal.core.relations.ItemRelation;
import org.rodinp.internal.core.relations.Relations.AttributeTypeRelations;
import org.rodinp.internal.core.relations.Relations.ElementTypeRelations;
import org.rodinp.internal.core.relations.RelationsComputer;
import org.rodinp.internal.core.relations.api.IAttributeType2;
import org.rodinp.internal.core.relations.api.IInternalElementType2;
import org.rodinp.internal.core.relations.tomerge.InternalElementType2;

/**
 * Acceptance tests of the relations API introduced by
 * {@link IInternalElementType2} and {@link IAttributeType2}.
 * 
 * @author Thomas Muller
 */
public class RelationsTests {

	/**
	 * Ensures that relationships can be reduced to a single pair. This also
	 * covers the case of a root element (<code>root</code>) and of a leaf
	 * element (<code>leaf</code>).
	 */
	@Test
	public void testOneChildRelation() {
		assertRelations("p1:c1:");
	}

	/**
	 * Ensures that a parent element type can be in a direct relationship two
	 * child element types.
	 */
	@Test
	public void testTwoChildrenRelation() {
		assertRelations("p2:c21,c22:");
	}

	/**
	 * Ensures that two different parent element types can be in a direct
	 * relationship the same child element type.
	 */
	@Test
	public void testTwoParentsRelation() {
		assertRelations("p21:c2:|p22:c2:");
	}

	/**
	 * Ensures that a direct cycle relationship can be defined for a given
	 * element type instance.
	 */
	@Test
	public void testCycle1Relation() {
		assertRelations("cy1:cy1:");
	}

	/**
	 * Ensures that an indirect cycle can exist within relationships between two
	 * element type instances.
	 */
	@Test
	public void testCycle2Relation() {
		assertRelations("cy21:cy22:|cy22:cy21:");
	}

	/**
	 * Ensures that an indirect cycle of length 3 can exist within relationships
	 * between three element type instances.
	 */
	@Test
	public void testCycle3Relation() {
		assertRelations("cy31:cy32:|cy32:cy33:|cy33:cy31:");
	}

	/**
	 * Ensures that a relationship can be reduced as a to a single pair
	 * consisting of a parent element type and an attribute type.
	 */
	@Test
	public void testAttributeRelation() {
		assertRelations("p3::a1");
	}

	/**
	 * Ensures that a parent-child relationship can be composed of both child
	 * attribute types and child element types.
	 */
	@Test
	public void testMixedChildAndAttributes() {
		assertRelations("p4:c4:a2");
	}

	private void assertRelations(String relationStrs) {
		final List<ItemRelation> itemRels = getItemRelations(relationStrs);
		final RelationsComputer c = new RelationsComputer();
		c.computeRelations(itemRels);
		c.setElementRelations();
		c.setAttributeRelations();
		final ElementTypeRelations expectedElemRels = getExpectedElemRelations(itemRels);
		final AttributeTypeRelations expectedAttrRels = getExpectedAttrRelations(itemRels);
		for (InternalElementType2<?> type : c.getElemTypes()) {
			assertArrayEquals(expectedElemRels.getParentTypes(type),
					type.getParentTypes());
			assertArrayEquals(expectedElemRels.getChildTypes(type),
					type.getChildTypes());
		}
		for (AttributeType<?> type : c.getAttributeTypes()) {
			assertArrayEquals(expectedAttrRels.getElementsTypes(type),
					type.getElementTypes());
		}
	}

	private ElementTypeRelations getExpectedElemRelations(
			List<ItemRelation> itemRelations) {
		final ElementTypeRelations eRels = new ElementTypeRelations();
		for (ItemRelation rel : itemRelations) {
			final InternalElementType2<?> parentType = rel.getParentType();
			final List<InternalElementType2<?>> childTypes = rel
					.getChildTypes();
			eRels.putAll(parentType, childTypes);
		}
		return eRels;
	}

	private AttributeTypeRelations getExpectedAttrRelations(
			List<ItemRelation> itemRelations) {
		final AttributeTypeRelations aRels = new AttributeTypeRelations();
		for (ItemRelation rel : itemRelations) {
			final InternalElementType2<?> parentType = rel.getParentType();
			final List<AttributeType<?>> childAttributes = rel
					.getAttributeTypes();
			aRels.putAll(parentType, childAttributes);
		}
		return aRels;
	}

	private static final Pattern REL_SEP_PAT = compile("\\|");

	private List<ItemRelation> getItemRelations(String relationsStrs) {
		final List<ItemRelation> relations = new ArrayList<ItemRelation>();
		final String[] relationStrs = REL_SEP_PAT.split(relationsStrs);
		for (String relation : relationStrs) {
			relations.add(relation(relation));
		}
		return relations;
	}

}
