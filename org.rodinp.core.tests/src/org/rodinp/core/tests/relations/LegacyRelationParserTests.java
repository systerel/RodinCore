/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests.relations;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.rodinp.core.tests.AbstractRodinDBTests.PLUGIN_ID;
import static org.rodinp.internal.core.relations.LegacyItemParsers.getLegacyAttributesMap;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.junit.Test;
import org.rodinp.internal.core.relations.ElementParser;
import org.rodinp.internal.core.relations.ItemRelation;
import org.rodinp.internal.core.relations.LegacyItemParsers;

/**
 * Unit tests for parsers in class {@link LegacyItemParsers}.
 * 
 * It ensures that the legacy item relations that are defined in the UI plug-in
 * from contributions to org.eventb.ui.editorItems extension point are correctly
 * parsed in order to be converted into item relations. These tests cover the
 * case of a parent-child element relationship, a parent-child attribute
 * relationship, and tests for the various kinds of legacy attribute references.
 * 
 * Legacy parsers are not tested in error: they are defined on the top of
 * parsers which inherit from {@link ElementParser} for which exhaustive testing
 * is performed {@link ItemRelationParserTests}.
 * 
 * @author Thomas Muller
 */
public class LegacyRelationParserTests extends AbstractItemRelationParserTests {

	private static final Map<String, String> EMPTY_ATTR_MAP = emptyMap();

	/**
	 * Ensures that a valid one-to-one parent-child relationship between two
	 * legacy element types <code>parent</code> and <code>child</code> is
	 * correctly parsed.
	 */
	@Test
	public void validLegacyChildItem() {
		assertLegacySuccess(
				t(node("childRelation : parentTypeId='parent'",
						node("childType : typeId='child'"))),
				relation("parent:child:"));
	}

	/**
	 * Ensures that a valid relationship between an element <code>parent</code>
	 * and an attribute <code>attrId</code> is correctly parsed, and that the
	 * type <code>attrType</code> of this attribute is correctly associated.
	 */
	@Test
	public void validLegacyAttributeItem() {
		assertLegacySuccess(
				t(node("attributeRelation : elementTypeId='parent'",
						node("attributeReference : descriptionId='attrId'"))),
				getLegacyAttributesMap(//
				t(node("toggleAttribute : id='attrId'; typeId='attrType'"))),
				relation("parent::attrType"));
	}

	/**
	 * Ensures that information from a toggleAttribute reference is correctly
	 * retrieved.
	 */
	@Test
	public void validToggleReferenceParse() {
		assertEquals(getAttributesMap("attrId", "attrType"),//
				getLegacyAttributesMap(//
				t(node("toggleAttribute : id='attrId'; typeId='attrType'"))));
	}

	/**
	 * Ensures that information from a textAttribute reference is correctly
	 * retrieved.
	 */
	@Test
	public void validTextReferenceParse() {
		assertEquals(getAttributesMap("attrId", "attrType"),//
				getLegacyAttributesMap(//
				t(node("textAttribute : id='attrId'; typeId='attrType'"))));
	}

	/**
	 * Ensures that information from a choiceAttribute reference is correctly
	 * retrieved.
	 */
	@Test
	public void validChoiceReferenceParse() {
		assertEquals(getAttributesMap("attrId", "attrType"),//
				getLegacyAttributesMap(//
				t(node("choiceAttribute : id='attrId'; typeId='attrType'"))));
	}

	/**
	 * Ensures that information from all kind of references which are mixed is
	 * correctly retrieved.
	 */
	@Test
	public void validAllReferencesParse() {
		assertLegacySuccess(
				t(//
				node("attributeRelation : elementTypeId='parent'", //
						node("attributeReference : descriptionId='attrId1'"), //
						node("attributeReference : descriptionId='attrId2'"), //
						node("attributeReference : descriptionId='attrId3'"))), //
				getLegacyAttributesMap(t(
						//
						node("textAttribute : id='attrId3'; typeId='a3Type'"), //
						node("choiceAttribute : id='attrId2'; typeId='a2Type'"), //
						node("toggleAttribute : id='attrId1'; typeId='a1Type'") //
				)), relation("parent::a1Type,a2Type,a3Type"));
	}

	protected void assertLegacySuccess(IConfigurationElement[] nodes,
			Map<String, String> attributesMap, ItemRelation... expecteds) {
		final boolean success = parser.parseLegacy(nodes, attributesMap);
		assertTrue("Unexpected errors " + parser.getErrors(), success);
		assertEquals(asList(expecteds), parser.getRelations());
	}

	protected void assertLegacySuccess(IConfigurationElement[] nodes,
			ItemRelation... expected) {
		assertLegacySuccess(nodes, EMPTY_ATTR_MAP, expected);
	}

	private Map<String, String> getAttributesMap(String key, String value) {
		final String prefix = PLUGIN_ID + ".";
		final Map<String, String> result = new HashMap<String, String>();
		result.put(prefix + key, prefix + value);
		return result;
	}

}
