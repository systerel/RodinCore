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
package org.rodinp.internal.core.relations;

import static org.rodinp.internal.core.util.Util.log;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.rodinp.internal.core.AttributeType;
import org.rodinp.internal.core.AttributeTypes;
import org.rodinp.internal.core.InternalElementType;
import org.rodinp.internal.core.InternalElementTypes;
import org.rodinp.internal.core.relations.ElementParser.RelationshipParser;

/**
 * Parser for the <code>itemRelations</code> extension point. Errors encountered
 * during parsing are available from {@link #getErrors()} to log them.
 * <p>
 * This class is not intended to work correctly for a dynamic aware plug-in (see
 * {@link IConfigurationElement}.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class ItemRelationParser {

	final List<ItemRelation> relations = new ArrayList<ItemRelation>();
	final List<String> errors = new ArrayList<String>();

	private final InternalElementTypes elementTypes;
	private final AttributeTypes attributeTypes;
	
	public ItemRelationParser(InternalElementTypes elementTypes, AttributeTypes attributeTypes) {
		this.elementTypes = elementTypes;
		this.attributeTypes = attributeTypes;
	}
	
	/**
	 * Parses the given configuration elements, stores the relations and returns
	 * <code>true</code> if no error occurred, <code>false</code> otherwise.
	 * 
	 * @param elems
	 *            the configuration elements to parse
	 * @return <code>true</code> if no error occurred during parsing,
	 *         <code>false</code> otherwise
	 */
	public boolean parse(IConfigurationElement[] elems) {
		final RelationshipParser parser = new RelationshipParser(this);
		final ElementListParser listParser = new ElementListParser(this,
				new ElementParser[] { parser });
		try {
			listParser.parse(elems);
		} catch (InvalidRegistryObjectException e) {
			log(e, "The plug-in has not been configured properly,"
					+ " this exception should not happen.");
		}
		return errors.isEmpty();
	}

	public List<ItemRelation> getRelations() {
		return relations;
	}

	/* package */void addRelation(ItemRelation relation) {
		relations.add(relation);
	}

	public List<String> getErrors() {
		return errors;
	}

	/* package */void addError(String message, IConfigurationElement elem) {
		errors.add(message + " from " + elem.getContributor().getName());
	}

	public InternalElementType<?> getInternalElementType(String id) {
		return elementTypes.get(id);
	}
	
	public AttributeType<?> getAttributeType(String id) {
		return attributeTypes.get(id);
	}

}
