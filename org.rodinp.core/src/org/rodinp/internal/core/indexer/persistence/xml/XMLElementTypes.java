/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer.persistence.xml;

import org.rodinp.internal.core.indexer.persistence.PersistenceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Beauger
 * 
 */
public enum XMLElementTypes {

	INDEX_ROOT, PIM, RODIN_INDEX, DESCRIPTOR, OCCURRENCE, EXPORT_TABLE, GRAPH,
	EXPORT, EXPORTED, NODE, PREDECESSOR, ITERATED, DELTA_LIST, DELTA,
	INDEXER_REGISTRY, REGISTRY_ENTRY, INDEXER, UNPROCESSED;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

	public static Element createElement(Document doc, XMLElementTypes name) {
		return doc.createElement(name.toString());
	}

	public static boolean hasName(Node node, XMLElementTypes name) {
		return node.getNodeName().equals(name.toString());
	}

	public static NodeList getElementsByTagName(Element node,
			XMLElementTypes nodeType) {
		return node.getElementsByTagName(nodeType.toString());
	}

	public static NodeList getElementsByTagName(Element node,
			XMLElementTypes nodeType, int expectedLength)
			throws PersistenceException {

		final NodeList result = node.getElementsByTagName(nodeType.toString());

		if (result.getLength() != expectedLength) {
			throw new PersistenceException();
		}
		return result;
	}

	public static void assertName(Node node, XMLElementTypes name)
			throws PersistenceException {
		if (!hasName(node, name)) {
			throw new PersistenceException();
		}
	}

}
