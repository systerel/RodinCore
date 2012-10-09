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
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.rodinp.core.RodinCore;
import org.rodinp.internal.core.InternalElementTypes;
import org.rodinp.internal.core.relations.ItemRelation;
import org.rodinp.internal.core.relations.Relations.ElementRelations;
import org.rodinp.internal.core.relations.RelationsComputer;

/**
 * Acceptance tests for the relation protocol on IInternalElementTypes.
 * 
 * FIXME currently uses the relation computer, but should use the new relation
 * API instead.
 * 
 * @author Thomas Muller
 */
public class RelationsTests {

	private static final String PREFIX = RodinCore.PLUGIN_ID + ".tests.";

	private static final String ITEM_SEP = ",";

	private static InternalTestTypes TYPES = new InternalTestTypes();

	/** Leaf or root */
	@Test
	public void testLeafRelation() {
		assertElementRelations("leaf-->");
	}

	/** One child */
	@Test
	public void testOneChildRelation() {
		assertElementRelations("p1-->c1");
	}

	/** Deux fils */
	@Test
	public void testTwoChildrenRelation() {
		assertElementRelations("p2-->c21,c22");
	}

	/** Deux pères */
	@Test
	public void testTwoParentsRelation() {
		assertElementRelations("p21,p22-->c2");
	}

	/** Cycle of length 1 */
	@Test
	public void testCycle1Relation() {
		assertElementRelations("cy1-->cy1");
	}

	/** Cycle of length 2 */
	@Test
	public void testCycle2Relation() {
		assertElementRelations("cy21-->cy22|cy22-->cy21");
	}

	/** Cycle of length 3 */
	@Test
	public void testCycle3Relation() {
		assertElementRelations("cy31-->cy32|cy32-->cy33|cy33-->cy31");
	}

	private void assertElementRelations(String relationSpecs) {
		final RelationsComputer c = new RelationsComputer(TYPES);
		final List<ItemRelation> itemRelations = getTestRelations(
				relationSpecs, TYPES);
		c.computeRelations(itemRelations);
		final ElementRelations actual = c.getElementRelations();
		final ElementRelations expected = getExpectedRelations(itemRelations,
				TYPES);
		assertEquals(expected, actual);
	}

	private ElementRelations getExpectedRelations(
			List<ItemRelation> itemRelations, InternalElementTypes types) {
		final ElementRelations relations = new ElementRelations(types);
		for (ItemRelation rel : itemRelations) {
			final String parentId = rel.getParentTypeId();
			final List<String> childrenTypeIds = rel.getChildTypeIds();
			relations.putAll(parentId, childrenTypeIds);
		}
		return relations;
	}

	private List<ItemRelation> getTestRelations(final String relationsStrs,
			final InternalTestTypes types) {
		final List<ItemRelation> relations = new ArrayList<ItemRelation>();
		final String[] relationStrs = relationsStrs.split("\\|");
		final Pattern p = compile("(\\S*)-->(\\S*)");
		for (String relation : relationStrs) {
			final Matcher matcher = p.matcher(relation);
			if (matcher.matches()) {
				final String parents = matcher.group(1);
				final String children = matcher.group(2);
				for (String parentId : parents.split(ITEM_SEP)) {
					final ItemRelation rel = new ItemRelation(PREFIX + parentId);
					for (String childId : children.split(ITEM_SEP)) {
						if (childId.isEmpty())
							continue;
						rel.addChildTypeId(PREFIX + childId);
					}
					relations.add(rel);
				}
			}
		}
		return relations;
	}

}
