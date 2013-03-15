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
package org.rodinp.internal.core.relations;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.internal.core.AttributeType;
import org.rodinp.internal.core.InternalElementType;

/**
 * Parsers for the legacy editorItems extensions contributed through the Event-B
 * UI plug-in. editorItems is the old extension point providing item relation
 * constraints to Rodin elements.
 * 
 * @author Thomas Muller
 */
public class LegacyItemParsers {

	private LegacyItemParsers() {
		// NO INSTANCE
	}

	private static final String CHILD_RELATION_ELEMENT_NAME = "childRelation";
	private static final String PARENT_TYPE_ID = "parentTypeId";
	private static final String CHILD_ELEMENT_NAME = "childType";

	private static final String ATTRIBUTE_RELATION_ELEMENT_NAME = "attributeRelation";
	private static final String ATTRIBUTE_REFERNCE_NAME = "attributeReference";

	private static final String ATTRIBUTE_PARENT_ID = "elementTypeId";
	private static final String ATTRIBUTE_REFERENCE_DESC_ID = "descriptionId";
	private static final String ATTRIBUTE_REFERENCE_ID = "id";
	private static final String TEXT_ATTRIBUTE_REFERENCE_NAME = "textAttribute";
	private static final String TOGGLE_ATTRIBUTE_REFERENCE_NAME = "toggleAttribute";
	private static final String CHOICE_ATTRIBUTE_REFERENCE_NAME = "choiceAttribute";

	// Identifier of both internal element types and attribute types from child
	// types or attribute references of the
	private static final String TYPE_ID = "typeId";

	public static class LegacyChildRelationParser extends ElementParser {

		public LegacyChildRelationParser(ItemRelationParser parent) {
			super(parent, CHILD_RELATION_ELEMENT_NAME, PARENT_TYPE_ID);
		}

		@Override
		protected void process(IConfigurationElement element,
				String parentTypeId) {
			final InternalElementType<?> parentType = parent
					.getInternalElementType(parentTypeId);
			if (parentType == null) {
				parent.addError("Unknown parent type '" + parentTypeId + "'",
						element);
				return;
			}
			final ItemRelation relation = new ItemRelation(parentType);
			final ElementListParser childrenParser = new ElementListParser(
					parent, new LegacyChildTypeParser(parent, relation));
			childrenParser.parse(element.getChildren());
			if (relation.isValid()) {
				parent.addRelation(relation);
			}
		}

	}

	public static class LegacyChildTypeParser extends ElementParser {

		private final ItemRelation relation;

		public LegacyChildTypeParser(ItemRelationParser parent,
				ItemRelation relation) {
			super(parent, CHILD_ELEMENT_NAME, TYPE_ID);
			this.relation = relation;
		}

		@Override
		protected void process(IConfigurationElement element, String elementId) {
			final InternalElementType<?> childType = parent
					.getInternalElementType(elementId);
			if (childType == null) {
				parent.addError("Unknown child element type '" + elementId
						+ "'", element);
				return;
			}
			relation.addChildType(childType);
		}

	}

	public static class LegacyAttributeRelationParser extends ElementParser {

		private final Map<String, String> attributesMap;

		public LegacyAttributeRelationParser(ItemRelationParser parent,
				Map<String, String> attributesMap) {
			super(parent, ATTRIBUTE_RELATION_ELEMENT_NAME, ATTRIBUTE_PARENT_ID);
			this.attributesMap = attributesMap;
		}

		@Override
		protected void process(IConfigurationElement element,
				String parentTypeId) {
			final InternalElementType<?> parentType = parent
					.getInternalElementType(parentTypeId);
			if (parentType == null) {
				parent.addError("Unknown parent type '" + parentTypeId + "'",
						element);
				return;
			}
			final ItemRelation relation = new ItemRelation(parentType);
			final ElementParser refParser = new LegacyAttributeReferenceParser(
					parent, relation, attributesMap);
			final ElementListParser attrParser = new ElementListParser(parent,
					refParser);
			attrParser.parse(element.getChildren());
			if (relation.isValid()) {
				parent.addRelation(relation);
			}
		}

	}

	public static class LegacyAttributeReferenceParser extends ElementParser {

		private final ItemRelation relation;
		private final Map<String, String> attributesMap;

		public LegacyAttributeReferenceParser(ItemRelationParser parent,
				ItemRelation relation, Map<String, String> attributesMap) {
			super(parent, ATTRIBUTE_REFERNCE_NAME, ATTRIBUTE_REFERENCE_DESC_ID);
			this.relation = relation;
			this.attributesMap = attributesMap;
		}

		@Override
		protected void process(IConfigurationElement element, String attributeId) {
			final AttributeType<?> attributeType = parent
					.getAttributeType(attributeId);
			if (attributeType == null) {
				parent.addError("Unknown attribute type '" + attributeId + "'",
						element);
				return;
			}
			relation.addAttributeType(attributeType);
		}

		@Override
		public void parse(IConfigurationElement element) {
			assert elementName.equals(element.getName());
			final String attributeDesc = element
					.getAttribute(ATTRIBUTE_REFERENCE_DESC_ID);
			final String attributeId = attributesMap.get(attributeDesc);
			process(element, attributeId);
		}

	}

	public static Map<String, String> getLegacyAttributesMap(
			IConfigurationElement[] elems) {
		final Map<String, String> result = new HashMap<String, String>();
		for (IConfigurationElement elem : elems) {
			if (isAttributeReference(elem)) {
				result.put(elem.getAttribute(ATTRIBUTE_REFERENCE_ID),
						elem.getAttribute(TYPE_ID));
			}
		}
		return result;
	}

	private static boolean isAttributeReference(IConfigurationElement elem) {
		final String name = elem.getName();
		return name.equals(TEXT_ATTRIBUTE_REFERENCE_NAME)
				|| name.equals(TOGGLE_ATTRIBUTE_REFERENCE_NAME)
				|| name.equals(CHOICE_ATTRIBUTE_REFERENCE_NAME);
	}

}
