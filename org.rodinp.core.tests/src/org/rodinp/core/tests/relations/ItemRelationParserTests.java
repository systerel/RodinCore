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
package org.rodinp.core.tests.relations;



import org.eclipse.core.runtime.IConfigurationElement;
import org.junit.Test;
import org.rodinp.internal.core.relations.ItemRelation;
import org.rodinp.internal.core.relations.ItemRelationParser;

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
public class ItemRelationParserTests extends AbstractItemRelationParserTests {

	private static final IConfigurationElement VALID_CHILD = node(
			"relationship : parentTypeId='p'",
			node("childType : typeId='child'"));
	private static final ItemRelation VALID_CHILD_REL = relation("p:child:");

	private static final IConfigurationElement VALID_ATTRIBUTE = node(
			"relationship : parentTypeId='p'",
			node("attributeType : typeId='attr'"));
	private static final ItemRelation VALID_ATTRIBUTE_REL = relation("p::attr");

	private static final ItemRelation VALID_BOTH_CHILDREN = relation("p:child:attr");

	private static final FakeConfigurationElement[] NONE = new FakeConfigurationElement[0];

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
	};

}
