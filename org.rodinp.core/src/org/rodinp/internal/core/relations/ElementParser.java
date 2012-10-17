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

import java.util.regex.Pattern;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.internal.core.AttributeType;
import org.rodinp.internal.core.relations.tomerge.InternalElementType2;

/**
 * Parsers for single configuration elements contributed to the
 * <code>itemRelations</code> extension points.
 * 
 * @author Laurent Voisin
 * @author Thomas Muller
 */
public abstract class ElementParser {

	private static Pattern SPACE_PATTERN = Pattern.compile("\\s");

	// Names of the relationship elements and children elements.
	// The name of a configuration element is the same as the XML tag of the
	// corresponding XML element.
	private static final String RELATION_ELEMENT_NAME = "relationship";
	private static final String CHILD_ELEMENT_NAME = "childType";
	private static final String ATTRIBUTE_ELEMENT_NAME = "attributeType";

	// The id of attributes to retrieve in relationships and their sub-elements
	private static final String PARENT_TYPE_ID = "parentTypeId";
	private static final String ITEM_TYPE_ID = "typeId";

	public static class RelationshipParser extends ElementParser {

		public RelationshipParser(ItemRelationParser parent) {
			super(parent, RELATION_ELEMENT_NAME, PARENT_TYPE_ID);
		}

		@Override
		protected void process(IConfigurationElement element,
				String parentTypeId) {
			final InternalElementType2<?> parentType = parent
					.getInternalElementType(parentTypeId);
			if (parentType == null) {
				parent.addError("Unknown parent type '" + parentTypeId + "'",
						element);
				return;
			}
			final ItemRelation relation = new ItemRelation(parentType);
			final ElementParser[] childParsers = new ElementParser[] {
					new ChildTypeParser(parent, relation),
					new AttributeTypeParser(parent, relation), };
			final ElementListParser childrenParser = new ElementListParser(
					parent, childParsers);
			childrenParser.parse(element.getChildren());
			if (relation.isValid()) {
				parent.addRelation(relation);
			}
		}

	}

	public static class ChildTypeParser extends ElementParser {

		private final ItemRelation relation;

		public ChildTypeParser(ItemRelationParser parent, ItemRelation relation) {
			super(parent, CHILD_ELEMENT_NAME, ITEM_TYPE_ID);
			this.relation = relation;
		}

		@Override
		protected void process(IConfigurationElement element, String elementId) {
			final InternalElementType2<?> childType = parent
					.getInternalElementType(elementId);
			if (childType == null) {
				parent.addError("Unknown child element type '" + elementId
						+ "'", element);
				return;
			}
			relation.addChildType(childType);
		}

	}

	public static class AttributeTypeParser extends ElementParser {

		private final ItemRelation relation;

		public AttributeTypeParser(ItemRelationParser parent,
				ItemRelation relation) {
			super(parent, ATTRIBUTE_ELEMENT_NAME, ITEM_TYPE_ID);
			this.relation = relation;
		}

		@Override
		protected void process(IConfigurationElement element, String attrName) {
			final AttributeType<?> attrType = parent.getAttributeType(attrName);
			if (attrType == null) {
				parent.addError("Unknown attribute type '" + attrName + "'",
						element);
				return;
			}
			relation.addAttributeType(attrType);
		}

	}

	protected final ItemRelationParser parent;
	protected final String elementName;
	private final String attributeName;

	public ElementParser(ItemRelationParser parent, String elementName,
			String attributeName) {
		this.parent = parent;
		this.elementName = elementName;
		this.attributeName = attributeName;
	}

	public void parse(IConfigurationElement element) {
		assert elementName.equals(element.getName());
		final String attributeValue = element.getAttribute(attributeName);
		if (attributeValue == null) {
			parent.addError("Missing attribute " + attributeName
					+ " in element " + elementName, element);
			return;
		}
		if (containsSpace(attributeValue)) {
			parent.addError("Invalid attribute value '" + attributeValue,
					element);
			return;
		}
		process(element, attributeValue);
	}

	protected abstract void process(IConfigurationElement element,
			String attributeValue);

	private boolean containsSpace(String string) {
		return SPACE_PATTERN.matcher(string).find();
	}

}
