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
public class IndexerRegPersistor {

	public static void save(Registry<String, String> indexerRegistry,
			Document doc, Element indexRoot) {
		final Element regNode = createElement(doc, INDEXER_REGISTRY);
		for (Entry<String, List<String>> entry : indexerRegistry.entrySet()) {
			final Element entryNode = createElement(doc, REGISTRY_ENTRY);
			RegEntryPersistor.save(entry, doc, entryNode);

			regNode.appendChild(entryNode);
		}
		indexRoot.appendChild(regNode);
	}

	public static void restore(Element indexRoot,
			Registry<String, String> indexerIdRegistry)
			throws PersistenceException {
		assertName(indexRoot, INDEX_ROOT);
		final NodeList regNodes = getElementsByTagName(indexRoot,
				INDEXER_REGISTRY, 1);
		final Element regNode = (Element) regNodes.item(0);
		
		final NodeList entryNodes = getElementsByTagName(regNode, REGISTRY_ENTRY);
		for (int i = 0; i < entryNodes.getLength(); i++) {
			final Element entryNode = (Element) entryNodes.item(i);
			RegEntryPersistor.restoreRegEntry(entryNode, indexerIdRegistry);
		}
	}

}
