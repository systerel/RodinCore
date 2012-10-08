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

import static java.util.regex.Pattern.compile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;

/**
 * Parser for the <code>itemRelations</code> extension point. Errors encountered
 * during parsing are available from {@link #getErrors()} to log them.
 * 
 * @author Laurent Voisin
 */
public class ItemRelationParser {

	// Names of the relationship elements and children elements.
	// The name of a configuration element is the same as the XML tag of the
	// corresponding XML element.
	private static final String RELATION_NAME = "relationship";
	private static final String CHILD_NAME = "childType";
	private static final String ATTR_NAME = "attributeType";

	// The id of attributes to retrieve in relationships and their sub-elements
	private static final String PARENT_TYPE_ID = "parentTypeId";
	private static final String ITEM_TYPE_ID = "typeId";

	final List<String> errors = new ArrayList<String>();

	public List<ItemRelation> parse(IConfigurationElement[] elems) {
		final List<ItemRelation> result = new ArrayList<ItemRelation>();
		for (final IConfigurationElement elem : elems) {
			try {
				final String name = elem.getName();
				if (!name.equals(RELATION_NAME)) {
					addInvalidItemError(elem);
					continue;
				}
				if (elem.getChildren().length == 0) {
					errors.add("Ignored empty relationship declaration for: "
							+ elem.getAttribute(PARENT_TYPE_ID));
					continue;
				}
				final ItemRelation parsedRelation = parse(elem);
				if (!parsedRelation.isValid()) {
					continue;
				}
				result.add(parsedRelation);
			} catch (InvalidRegistryObjectException e) {
				addInvalidConfigElementError(elem, e);
			}
		}
		return result;
	}

	// public for testing purpose
	public ItemRelation parse(IConfigurationElement relationElem) {
		String parentId = null;
		try {
			parentId = getAttribute(relationElem, PARENT_TYPE_ID);
		} catch (InvalidRegistryObjectException e) {
			addInvalidConfigElementError(relationElem, e);
		}
		final ItemRelation relation = new ItemRelation(parentId);
		parseRelationChildren(relationElem, relation);
		return relation;
	}

	private void parseRelationChildren(IConfigurationElement relationElem,
			ItemRelation relation) {
		for (IConfigurationElement elem : relationElem.getChildren()) {
			try {
				final String elemName = elem.getName();
				if (elemName.equals(CHILD_NAME)) {
					final String childId = getAttribute(elem, ITEM_TYPE_ID);
					if (childId != null) {
						relation.addChildTypeId(childId);
					}
				} else if (elemName.equals(ATTR_NAME)) {
					final String attrId = getAttribute(elem, ITEM_TYPE_ID);
					if (attrId != null) {
						relation.addAttributeTypeId(attrId);
					}
				} else {
					errors.add("Ignored invalid relation item: " + elemName);
				}
			} catch (InvalidRegistryObjectException e) {
				addInvalidConfigElementError(relationElem, e);
			}
		}
	}

	private String getAttribute(IConfigurationElement elem, String attributeId) {
		final String attr = elem.getAttribute(attributeId);
		if (isValid(attr)) {
			return attr;
		}
		addInvalidItemError(elem);
		return null;
	}

	private boolean isValid(String childId) {
		if (childId == null)
			return false;
		final Pattern p = compile("\\s");
		final boolean matches = p.matcher(childId).find();
		return !matches;
	}

	private void addInvalidConfigElementError(IConfigurationElement elem,
			InvalidRegistryObjectException e) {
		errors.add("Invalid relationship item exception occured with: " + elem
				+ ". Exception: " + e.getMessage());
	}

	private void addInvalidItemError(IConfigurationElement elem) {
		errors.add("Ignored element named " + elem.getName() + " from "
				+ elem.getContributor().getName());
	}

	public List<String> getErrors() {
		return errors;
	}

}
