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
import static org.junit.Assert.assertNotNull;
import static org.rodinp.core.tests.relations.ItemRelationParserTests.PREFIX;
import static org.rodinp.core.tests.relations.ItemRelationParserTests.aTypes;
import static org.rodinp.core.tests.relations.ItemRelationParserTests.eTypes;
import static org.rodinp.core.tests.relations.ItemRelationParserTests.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;
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
	 * Ensures that a parent element type can be in a direct relationship with
	 * two child element types.
	 */
	@Test
	public void testTwoChildrenRelation() {
		assertRelations("p2:c21,c22:");
	}

	/**
	 * Ensures that two different parent element types can be in a direct
	 * relationship with the same child element type.
	 */
	@Test
	public void testTwoParentsRelation() {
		assertRelations("p21:c2:|p22:c2:");
	}

	/**
	 * Ensures that an element can parent an element of the same type (cycle of
	 * length 1).
	 */
	@Test
	public void testCycle1Relation() {
		assertRelations("cy1:cy1:");
	}

	/**
	 * Ensures that a cycle of length 2 is supported.
	 */
	@Test
	public void testCycle2Relation() {
		assertRelations("cy21:cy22:|cy22:cy21:");
	}

	/**
	 * Ensures that a cycle of length 3 is supported.
	 */
	@Test
	public void testCycle3Relation() {
		assertRelations("cy31:cy32:|cy32:cy33:|cy33:cy31:");
	}

	/**
	 * Ensures that attribute relationships are supported.
	 */
	@Test
	public void testAttributeRelation() {
		assertRelations("p3::a1");
	}

	/**
	 * Ensures that a relationship with both child and attribute types is
	 * supported.
	 */
	@Test
	public void testMixedChildAndAttributes() {
		assertRelations("p4:c4:a2");
	}

	/**
	 * Ensures that API methods that return an array are not disrupted by
	 * clients modifying the returned array.
	 */
	@Test
	public void testAPIGetters() {
		computeItemRelations("p5:c5:a5");
		final IInternalElementType2<?> p5 = getInternalElementType("p5");
		final IInternalElementType2<?> c5 = getInternalElementType("c5");
		final IAttributeType2 a5 = getAttributeType("a5");
		new ChildTypeMutator().test(p5);
		new ParentTypeMutator().test(c5);
		new AttrTypeMutator().test(p5);
		new ElemTypeMutator().test(a5);
	}

	private IInternalElementType2<?> getInternalElementType(String shortId) {
		return (IInternalElementType2<?>) eTypes.get(PREFIX + shortId);
	}

	private IAttributeType2 getAttributeType(String shortId) {
		return aTypes.get(PREFIX + shortId);
	}

	/**
	 * Common implementation for checking that an array returned by a method
	 * call can be changed by the client without impacting later calls to the
	 * same method.
	 * 
	 * @param <T>
	 *            class containing the method returning an array
	 * @param <U>
	 *            type of the elements of the array
	 */
	private static abstract class Mutator<T, U> {
		public void test(T itemType) {
			final U[] firstArray = getArray(itemType);
			final U[] expected = firstArray.clone();
			assertNotNull(firstArray[0]);
			firstArray[0] = null;
			final U[] actual = getArray(itemType);
			assertArrayEquals(expected, actual);
		}

		protected abstract U[] getArray(T itemType);
	}

	private static class ChildTypeMutator extends
			Mutator<IInternalElementType2<?>, IInternalElementType<?>> {
		@Override
		protected IInternalElementType<?>[] getArray(
				IInternalElementType2<?> itemType) {
			return itemType.getChildTypes();
		}
	}

	private static class ParentTypeMutator extends
			Mutator<IInternalElementType2<?>, IInternalElementType<?>> {
		@Override
		protected IInternalElementType<?>[] getArray(
				IInternalElementType2<?> itemType) {
			return itemType.getParentTypes();
		}
	}

	private static class AttrTypeMutator extends
			Mutator<IInternalElementType2<?>, IAttributeType> {
		@Override
		protected IAttributeType[] getArray(IInternalElementType2<?> itemType) {
			return itemType.getAttributeTypes();
		}
	}

	private static class ElemTypeMutator extends
			Mutator<IAttributeType2, IInternalElementType<?>> {
		@Override
		protected IInternalElementType<?>[] getArray(IAttributeType2 itemType) {
			return itemType.getElementTypes();
		}
	}

	private List<ItemRelation> itemRels;
	private RelationsComputer computer = new RelationsComputer();

	private void computeItemRelations(String relationStrs) {
		itemRels = getItemRelations(relationStrs);
		computer.setRelations(itemRels);
	}

	private void assertRelations(String relationStrs) {
		computeItemRelations(relationStrs);
		final ElementTypeRelations expectedElemRels = getExpectedElemRelations(itemRels);
		final AttributeTypeRelations expectedAttrRels = getExpectedAttrRelations(itemRels);
		for (InternalElementType2<?> type : computer.getElemTypes()) {
			assertArrayEquals(expectedElemRels.getParentTypes(type),
					type.getParentTypes());
			assertArrayEquals(expectedElemRels.getChildTypes(type),
					type.getChildTypes());
		}
		for (AttributeType<?> type : computer.getAttributeTypes()) {
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
