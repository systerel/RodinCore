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
import static org.rodinp.core.tests.relations.ItemRelationParserTests.PREFIX;
import static org.rodinp.core.tests.relations.ItemRelationParserTests.aTypes;
import static org.rodinp.core.tests.relations.ItemRelationParserTests.eTypes;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;
import org.rodinp.internal.core.InternalElementType;
import org.rodinp.internal.core.relations.ItemRelation;
import org.rodinp.internal.core.relations.Relations.AttributeTypesRelations;
import org.rodinp.internal.core.relations.Relations.ElementTypesRelations;
import org.rodinp.internal.core.relations.RelationsComputer;
import org.rodinp.internal.core.relations.api.IAttributeType2;
import org.rodinp.internal.core.relations.tomerge.InternalElementType2;

/**
 * Acceptance tests for the relation protocol on {@code IInternalElementType2}.
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
		assertRelations("p1>>c1");
	}

	/**
	 * Ensures that a parent element type can be in a direct relationship two
	 * child element types.
	 */
	@Test
	public void testTwoChildrenRelation() {
		assertRelations("p2>>c21,c22");
	}

	/**
	 * Ensures that two different parent element types can be in a direct
	 * relationship the same child element type.
	 */
	@Test
	public void testTwoParentsRelation() {
		assertRelations("p21,p22>>c2");
	}

	/**
	 * Ensures that a direct cycle relationship can be defined for a given
	 * element type instance.
	 */
	@Test
	public void testCycle1Relation() {
		assertRelations("cy1>>cy1");
	}

	/**
	 * Ensures that an indirect cycle can exist within relationships between two
	 * element type instances.
	 */
	@Test
	public void testCycle2Relation() {
		assertRelations("cy21>>cy22|cy22>>cy21");
	}

	/**
	 * Ensures that an indirect cycle of length 3 can exist within relationships
	 * between three element type instances.
	 */
	@Test
	public void testCycle3Relation() {
		assertRelations("cy31>>cy32|cy32>>cy33|cy33>>cy31");
	}

	/**
	 * Ensures that a relationship can be reduced as a to a single pair
	 * consisting of a parent element type and an attribute type.
	 */
	@Test
	public void testAttributeRelation() {
		assertRelations("p3>>a1");
	}
	
	/**
	 * Ensures that a parent-child relationship can be composed of both child
	 * attribute types and child element types.
	 */
	@Test
	public void testMixedChildAndAttributes() {
		assertRelations("p4>>c4,a2");
	}

	private void assertRelations(String relationStrs) {
		final List<ItemRelation> itemRels = getItemRelations(relationStrs);
		final IInternalElementType<?>[] testedElemTypes = getTestedElemTypes(relationStrs);
		final IAttributeType[] testedAttrTypes = getTestedAttrTypes(relationStrs);
		final RelationsComputer c = new RelationsComputer();
		c.computeRelations(itemRels);
		eTypes.setRelations(c, testedElemTypes);
		aTypes.setRelations(c, testedAttrTypes);
		final ElementTypesRelations expectedElemRels = getExpectedElemRelations(itemRels);
		final AttributeTypesRelations expectedAttrRels = getExpectedAttrRelations(
				itemRels, testedAttrTypes);
		for (IInternalElementType<?> type : testedElemTypes) {
			final InternalElementType2<?> testedType = (InternalElementType2<?>) type;
			assertArrayEquals(expectedElemRels.getParentTypes(testedType),
					testedType.getParentTypes());
			assertArrayEquals(expectedElemRels.getChildTypes(testedType),
					testedType.getChildTypes());
		}
		for (IAttributeType type : testedAttrTypes) {
			final IAttributeType2 testedType = (IAttributeType2) type;
			assertArrayEquals(expectedAttrRels.getElementsTypes(testedType),
					testedType.getElementTypes());
		}
	}

	private static final Pattern IDENT_SEP_PAT = compile("\\||>>|,");

	private IInternalElementType<?>[] getTestedElemTypes(String relationsStr) {
		final String[] idents = IDENT_SEP_PAT.split(relationsStr);
		final Set<IInternalElementType<?>> set = new LinkedHashSet<IInternalElementType<?>>();
		for (String id : idents) {
			if (isAnElement(id)) {
				set.add(eTypes.get(PREFIX + id));
			}
		}
		return set.toArray(new IInternalElementType<?>[set.size()]);
	}

	private IAttributeType[] getTestedAttrTypes(String relationStr) {
		final String[] idents = IDENT_SEP_PAT.split(relationStr);
		final Set<IAttributeType> set = new LinkedHashSet<IAttributeType>();
		for (String id : idents) {
			if (isAnAttribute(id)) {
				set.add(aTypes.get(PREFIX + id));
			}
		}
		return set.toArray(new IAttributeType[set.size()]);
	}

	private ElementTypesRelations getExpectedElemRelations(
			List<ItemRelation> itemRelations) {
		final ElementTypesRelations eRels = new ElementTypesRelations();
		for (ItemRelation rel : itemRelations) {
			final IInternalElementType<?> parentType = rel.getParentType();
			final List<IInternalElementType<?>> childTypes = rel
					.getChildTypes();
			eRels.putAll(parentType, childTypes);
		}
		return eRels;
	}

	private AttributeTypesRelations getExpectedAttrRelations(
			List<ItemRelation> itemRelations, IAttributeType[] types) {
		final AttributeTypesRelations aRels = new AttributeTypesRelations();
		for (ItemRelation rel : itemRelations) {
			final IInternalElementType<?> parentType = rel.getParentType();
			final List<IAttributeType> childAttributes = rel
					.getAttributeTypes();
			aRels.putAll(parentType, childAttributes);
		}
		return aRels;
	}

	private static final Pattern REL_SEP_PAT = compile("\\|");
	private static final Pattern RELATION_PATTERN = compile("(\\S*)>>(\\S*)");
	private static final Pattern ITEM_SEP_PAT = compile(",");

	private List<ItemRelation> getItemRelations(String relationsStrs) {
		final List<ItemRelation> relations = new ArrayList<ItemRelation>();
		final String[] relationStrs = REL_SEP_PAT.split(relationsStrs);
		for (String relation : relationStrs) {
			final Matcher matcher = RELATION_PATTERN.matcher(relation);
			if (!matcher.matches()) {
				continue;
			}
			final String parents = matcher.group(1);
			final String children = matcher.group(2);
			for (String parentId : ITEM_SEP_PAT.split(parents)) {
				final InternalElementType<?> parent = //
				eTypes.get(PREFIX + parentId);
				final ItemRelation rel = new ItemRelation(parent);
				for (String childId : ITEM_SEP_PAT.split(children)) {
					if (childId.isEmpty())
						continue;
					if (isAnElement(childId)) {
						rel.addChildType(eTypes.get(PREFIX + childId));
					}
					if (isAnAttribute(childId)) {
						rel.addAttributeType(aTypes.get(PREFIX + childId));
					}
				}
				relations.add(rel);
			}
		}
		return relations;
	}

	private boolean isAnElement(String itemId) {
		return itemId.startsWith("c");
	}

	private boolean isAnAttribute(String itemId) {
		return itemId.startsWith("a");
	}

}
