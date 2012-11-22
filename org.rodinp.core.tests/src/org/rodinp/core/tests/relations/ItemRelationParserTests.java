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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.rodinp.core.tests.AbstractRodinDBTests.PLUGIN_ID;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IConfigurationElement;
import org.junit.Test;
import org.rodinp.internal.core.ElementTypeManager;
import org.rodinp.internal.core.relations.ItemRelation;
import org.rodinp.internal.core.relations.ItemRelationParser;
import org.rodinp.internal.core.relations.tomerge.InternalElementType2;

/**
 * Unit tests for class {@link ItemRelationParser}.
 * 
 * It covers the following <em>successful cases</em>:
 * <ul>
 * <li>a relation with a valid child item,</li>
 * <li>a relation with a valid child attribute,</li>
 * <li>a relation with both a valid child item and a valid child attribute.</li>
 * </ul>
 * 
 * It covers inner (resp. outer relation) <em>failure cases</em> due to
 * ill-formed configuration elements and asserts that do not have side-effects
 * on the parent relation (resp. on valid relations aside).<br>
 * The types of failure tested are the following :
 * <ul>
 * <li>unknown tag,</li>
 * <li>missing attribute,</li>
 * <li>invalid attribute value.</li>
 * </ul>
 * 
 * @author Thomas Muller
 */
public class ItemRelationParserTests {

	public static final String PREFIX = PLUGIN_ID + ".";

	public static final ElementTypeManager typeManager = ElementTypeManager
			.getInstanceForTests();
	public static final InternalTestTypes eTypes = new InternalTestTypes(
			typeManager);
	public static final AttributeTestTypes aTypes = new AttributeTestTypes(typeManager);

	private static final IConfigurationElement VALID_CHILD = node(
			"relationship : parentTypeId='p'",
			node("childType : typeId='child'"));
	private static final ItemRelation VALID_CHILD_REL = relation("p:child:");

	private static final IConfigurationElement VALID_ATTRIBUTE = node(
			"relationship : parentTypeId='p'",
			node("attributeType : typeId='attr'"));
	private static final ItemRelation VALID_ATTRIBUTE_REL = relation("p::attr");

	private static final ItemRelation VALID_BOTH_CHILDREN = relation("p:child:attr");

	private static final String REL_ATTR_SEP = "\\s*:\\s*";
	private static final String ATTR_SEP = "\\s*;\\s*";
	private static final FakeConfigurationElement[] NONE = new FakeConfigurationElement[0];

	private final ItemRelationParser parser = new ItemRelationParser(eTypes,
			aTypes);

	/**
	 * Ensures that a valid one-to-one parent-child relationship between two
	 * element types <code>p</code> and <code>child</code> is correctly parsed.
	 */
	@Test
	public void validChildItem() {
		assertSuccess( //
				t(VALID_CHILD), VALID_CHILD_REL);
	}

	/**
	 * Ensures that a valid one-to-one parent-child relationship between an
	 * element <code>p</code> and an attribute <code>attr</code> is correctly
	 * parsed.
	 */
	@Test
	public void validChildAttribute() {
		assertSuccess(//
				t(VALID_ATTRIBUTE), VALID_ATTRIBUTE_REL);
	}

	/**
	 * Ensures that a one-to-many mixed parent-child relationship is correctly
	 * parsed.
	 */
	@Test
	public void validBothChildren() {
		assertSuccess(t(//
				node("relationship : parentTypeId='p'", //
						node("childType : typeId='child'"), //
						node("attributeType : typeId='attr'"))//
				), //
				VALID_BOTH_CHILDREN);
	}

	/**
	 * Ensures that an unknown child element is detected and ignored and does
	 * not disturb the parsing of the relationship it is defined in.
	 */
	@Test
	public void unknownChildElementRelationFailure() {
		assertFailure(t( //
				node("relationship : parentTypeId='p'",
						node("childType : typeId='child'"), //
						node("unknowChild : typeId='child2'"), //
						node("attributeType : typeId='attr'"))// ), //
				), //
				VALID_BOTH_CHILDREN);
	}

	/**
	 * Ensures that an ill-formed child element with a missing attribute is
	 * detected and ignored and does not disturb the parsing of the relationship
	 * it is defined in.
	 */
	@Test
	public void missingAttributeChildElementRelationFailure() {
		assertFailure(t( //
				node("relationship : parentTypeId='p'",
						node("childType : typeId='child'"), //
						node("childType : notATypeIdAttr='child2'"), //
						node("attributeType : typeId='attr'"))// ), //
				), //
				VALID_BOTH_CHILDREN);
	}

	/**
	 * Ensures that an ill-formed child element with an invalid attribute is
	 * ignored and does not disturb the parsing of the relationship it is
	 * defined in.
	 */
	@Test
	public void invalidValueChildAttributeRelationFailure() {
		assertFailure(t( //
				node("relationship : parentTypeId='p'",
						node("childType : typeId='child'"), //
						node("attributeType : typeId='invalid attr'"), //
						node("attributeType : typeId='attr'"))// ), //
				), //
				VALID_BOTH_CHILDREN);
	}

	/**
	 * Ensures that an empty relationship (case: unknown child tag) is detected
	 * and ignored, and does not disturb the parsing of surrounding valid
	 * relationships.
	 */
	@Test
	public void unknownChildFailure() {
		assertFailure(t( //
				VALID_CHILD, //
				node("relationship : parentTypeId='p'", //
						node("unknowChildTag : typeId='child'")), //
				VALID_ATTRIBUTE //
				), //
				VALID_CHILD_REL, //
				VALID_ATTRIBUTE_REL);
	}

	/**
	 * Ensures that an empty relationship (case: not a relationship element) is
	 * detected and ignored, and does not disturb the parsing of surrounding
	 * valid relationships.
	 */
	@Test
	public void unknownTagFailure() {
		assertFailure(t( //
				VALID_CHILD, //
				node("badRelationship : parentTypeId='p'", NONE), //
				VALID_ATTRIBUTE //
				), //
				VALID_CHILD_REL, //
				VALID_ATTRIBUTE_REL);
	}

	/**
	 * Ensures that an empty relationship (case: invalid parent type id) is
	 * detected and ignored, and does not disturb the parsing of surrounding
	 * valid relationships.
	 */
	@Test
	public void missingRelationId() {
		assertFailure(t(//
				VALID_CHILD, //
				node("relationship : badParentTypeId='p'", NONE), //
				VALID_ATTRIBUTE //
				), //
				VALID_CHILD_REL, //
				VALID_ATTRIBUTE_REL);
	}

	/**
	 * Ensures that an empty relationship (case: invalid parent type id value)
	 * is detected and ignored, and does not disturb the parsing of surrounding
	 * valid relationships.
	 */
	@Test
	public void invalidRelationValueFailure() {
		assertFailure(t( //
				VALID_CHILD, //
				node("relationship : parentTypeId='   p'", NONE), //
				VALID_ATTRIBUTE //
				), //
				VALID_CHILD_REL, //
				VALID_ATTRIBUTE_REL);
	}

	/**
	 * Ensures that an empty relationship (case: missing id attribute of the
	 * child element type) is detected and ignored, and does not disturb the
	 * parsing of surrounding valid relationships.
	 */
	@Test
	public void missingChildAttributeFailure() {
		assertFailure(t( //
				VALID_CHILD, //
				node("relationship : parentTypeId='p'", //
						node("childType: badAttr='attr'")), //
				VALID_ATTRIBUTE //
				), //
				VALID_CHILD_REL, //
				VALID_ATTRIBUTE_REL);
	}

	/**
	 * Ensures that an empty relationship (case: invalid child element type
	 * value) is detected and ignored, and does not disturb the parsing of
	 * surrounding valid relationships.
	 */
	@Test
	public void invalidChildValueFailure() {
		assertFailure(t( //
				VALID_CHILD, //
				node("relationship : parentTypeId='p'", //
						node("childType: typeId='   child'")), //
				VALID_ATTRIBUTE //
				), //
				VALID_CHILD_REL, //
				VALID_ATTRIBUTE_REL);
	}

	/**
	 * Ensures that an empty relationship (case: missing id attribute of the
	 * child attribute type) is detected and ignored, and does not disturb the
	 * parsing of surrounding valid relationships.
	 */
	@Test
	public void missingAttributeFailure() {
		assertFailure(t( //
				VALID_CHILD, //
				node("relationship : parentTypeId='p'", //
						node("attributeType: badAttr='attr'")), //
				VALID_ATTRIBUTE //
				), //
				VALID_CHILD_REL, //
				VALID_ATTRIBUTE_REL);
	}

	/**
	 * Ensures that an empty relationship (case: invalid id attribute value of
	 * the child attribute type ) is detected and ignored, and does not disturb
	 * the parsing of surrounding valid relationships.
	 */
	@Test
	public void invalidAttributeValueFailure() {
		assertFailure(t( //
				VALID_CHILD, //
				node("relationship : parentTypeId='p'", //
						node("attributeType: typeId='at  tr'")), //
				VALID_ATTRIBUTE //
				), //
				VALID_CHILD_REL, //
				VALID_ATTRIBUTE_REL);
	}

	private void assertSuccess(IConfigurationElement[] nodes,
			ItemRelation... expected) {
		assertTrue(parser.parse(nodes));
		final List<ItemRelation> actual = parser.getRelations();
		assertEquals(expected.length, actual.size());
		int i = 0;
		for (ItemRelation rel : expected) {
			assertEquals(rel, actual.get(i));
			i++;
		}
	}

	private void assertFailure(IConfigurationElement[] nodes,
			ItemRelation... expected) {
		assertFalse(parser.parse(nodes));
		final List<ItemRelation> relations = parser.getRelations();
		assertEquals(expected.length, relations.size());
		int i = 0;
		for (ItemRelation rel : expected) {
			assertEquals(rel, relations.get(i));
			i++;
		}
	}

	private static IConfigurationElement node(String nodeSpec,
			IConfigurationElement... children) {
		final String[] specs = nodeSpec.split(REL_ATTR_SEP);
		final String nodeName = specs[0];
		final String[] attributeStrs = getIDs(specs[1].split(ATTR_SEP));
		return new FakeConfigurationElement(nodeName, attributeStrs, children);
	}

	private static String[] getIDs(String[] ids) {
		final String[] result = new String[ids.length];
		final String separator = "='";
		for (int i = 0; i < ids.length; i++) {
			result[i] = ids[i].replaceAll(separator, separator + PREFIX);
		}
		return result;
	}

	/*package*/ static ItemRelation relation(String relationImage) {
		final Pattern p = compile("(\\S+):(\\S*):(\\S*)");
		final Matcher m = p.matcher(relationImage);
		if (m.matches()) {
			final String parentId = m.group(1);
			final ItemRelation itemRelation = new ItemRelation(
					(InternalElementType2<?>) eTypes.get(p(parentId)));
			final Pattern p2 = compile(",");
			final String children = m.group(2);
			for (String child : p2.split(children)) {
				if (child.isEmpty())
					continue;
				itemRelation.addChildType((InternalElementType2<?>) eTypes
						.get(p(child)));				
			}
			final String attributes = m.group(3);
			for (String attr : p2.split(attributes)) {
				if (attr.isEmpty())
					continue;
				itemRelation.addAttributeType(aTypes.get(p(attr)));				
			}
			return itemRelation;
		}
		return null;
	}

	private static String p(String string) {
		return PREFIX + string;
	}

	private static <T> T[] t(T... ts) {
		return ts;
	};

}
