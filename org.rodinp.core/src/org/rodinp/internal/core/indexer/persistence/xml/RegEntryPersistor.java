/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer.persistence.xml;

import static org.rodinp.internal.core.indexer.persistence.xml.XMLAttributeTypes.*;
import static org.rodinp.internal.core.indexer.persistence.xml.XMLElementTypes.*;

import java.util.List;
import java.util.Map.Entry;

import org.rodinp.internal.core.indexer.Registry;
import org.rodinp.internal.core.indexer.persistence.PersistenceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Beauger
 * 
 */
public class RegEntryPersistor {

	public static void save(Entry<String, List<String>> entry, Document doc,
			Element entryNode) {
		final String rootTypeId = entry.getKey();
		final List<String> indexerIds = entry.getValue();

		setAttribute(entryNode, ELEMENT_TYPE, rootTypeId);

		for (String indexerId : indexerIds) {
			final Element indexerNode = createElement(doc, INDEXER);
			IndexerPersistor.save(indexerId, indexerNode);

			entryNode.appendChild(indexerNode);
		}
	}

	public static void restoreRegEntry(Element entryNode,
			Registry<String, String> indexerIdRegistry) throws PersistenceException {
		final String rootTypeId = getAttribute(entryNode, ELEMENT_TYPE);
		
		final NodeList indexerNodes = getElementsByTagName(entryNode, INDEXER);
		for (int i = 0; i < indexerNodes.getLength(); i++) {
			final Element indexerNode = (Element) indexerNodes.item(i);
			final String indexerId = IndexerPersistor.getIndexerId(indexerNode);
			
			indexerIdRegistry.add(rootTypeId, indexerId);
		}

	}
}
