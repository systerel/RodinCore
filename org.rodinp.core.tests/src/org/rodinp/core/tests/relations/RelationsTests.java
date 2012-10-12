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
import org.rodinp.internal.core.AttributeTypes;
import org.rodinp.internal.core.InternalElementType;
import org.rodinp.internal.core.InternalElementTypes;
import org.rodinp.internal.core.relations.ItemRelation;
import org.rodinp.internal.core.relations.Relations.AttributeTypesRelations;
import org.rodinp.internal.core.relations.Relations.ElementTypesRelations;
import org.rodinp.internal.core.relations.tomerge.InternalElementType2;

/**
 * Acceptance tests for the relation protocol on {@code IInternalElementType2}.
 * 
 * @author Thomas Muller
 */
public class RelationsTests {

	/** Leaf or root */
	@Test
	public void testLeafRelation() {
		assertElementRelations("leaf>>");
	}

	/** One child */
	@Test
	public void testOneChildRelation() {
		assertElementRelations("p1>>a1,c1,a2");
	}

	/** Two children */
	@Test
	public void testTwoChildrenRelation() {
		assertElementRelations("p2>>c21,c22,a2");
	}

	/** Two parents */
	@Test
	public void testTwoParentsRelation() {
		assertElementRelations("p21,p22>>c2,a2");
	}

	/** Cycle of length 1 */
	@Test
	public void testCycle1Relation() {
		assertElementRelations("cy1>>cy1");
	}

	/** Cycle of length 2 */
	@Test
	public void testCycle2Relation() {
		assertElementRelations("cy21>>cy22|cy22>>cy21");
	}

	/** Cycle of length 3 */
	@Test
	public void testCycle3Relation() {
		assertElementRelations("cy31>>cy32|cy32>>cy33|cy33>>cy31");
	}

	private void assertElementRelations(String relationStrs) {
		final List<ItemRelation> itemRels = getItemRelations(relationStrs);
		final IInternalElementType<?>[] testedTypes = getTestedElementTypes(relationStrs);
		eTypes.computeRelations(itemRels, testedTypes);
		// attribute relations are computed at the same time as element
		// relations
		final ElementTypesRelations expectedElemRels = getExpectedElementRelations(
				itemRels, eTypes);
		final AttributeTypesRelations expectedAttrRels = getExpectedAttributeRelations(
				itemRels, aTypes);
		for (IInternalElementType<?> type : testedTypes) {
			final InternalElementType2<?> testedType = (InternalElementType2<?>) type;
			assertArrayEquals(expectedElemRels.getParentTypes(testedType),
					testedType.getParentTypes());
			assertArrayEquals(expectedElemRels.getChildTypes(testedType),
					testedType.getChildTypes());
			assertArrayEquals(expectedAttrRels.getAttributes(testedType),
					testedType.getAttributeTypes());
		}
	}

	private static final Pattern IDENT_SEP_PAT = compile("\\||>>|,");

	private IInternalElementType<?>[] getTestedElementTypes(
			String relationsSpecs) {
		final String[] idents = IDENT_SEP_PAT.split(relationsSpecs);
		final Set<IInternalElementType<?>> set = new LinkedHashSet<IInternalElementType<?>>();
		for (String id : idents) {
			if (isElementId(id)) {
				set.add(eTypes.get(PREFIX + id));
			}
		}
		return set.toArray(new IInternalElementType<?>[set.size()]);
	}

	private boolean isElementId(String id) {
		return id.startsWith("c");
	}

	private boolean isAttributeId(String string) {
		return string.startsWith("a");
	}

	private ElementTypesRelations getExpectedElementRelations(
			List<ItemRelation> itemRelations, InternalElementTypes types) {
		final ElementTypesRelations eRels = new ElementTypesRelations();
		for (ItemRelation rel : itemRelations) {
			final IInternalElementType<?> parentType = rel.getParentType();
			final List<IInternalElementType<?>> childTypes = rel
					.getChildTypes();
			eRels.putAll(parentType, childTypes);
		}
		return eRels;
	}

	private AttributeTypesRelations getExpectedAttributeRelations(
			List<ItemRelation> itemRelations, AttributeTypes types) {
		final AttributeTypesRelations aRels = new AttributeTypesRelations();
		for (ItemRelation rel : itemRelations) {
			final IInternalElementType<?> parentType = rel.getParentType();
			final List<IAttributeType> childTypes = rel.getAttributeTypes();
			aRels.putAll(parentType, childTypes);
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
					if (isElementId(childId)) {
						final InternalElementType<?> child = eTypes.get(PREFIX
								+ childId);
						rel.addChildType(child);
					}
					if (isAttributeId(childId)) {
						final IAttributeType attr = aTypes
								.get(PREFIX + childId);
						rel.addAttributeType(attr);
					}
				}
				relations.add(rel);
			}
		}
		return relations;
	}

}
