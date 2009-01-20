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

import static org.rodinp.internal.core.indexer.persistence.xml.XMLElementTypes.*;

import java.util.Collection;

import org.rodinp.internal.core.indexer.IIndexDelta;
import org.rodinp.internal.core.indexer.persistence.PersistenceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Beauger
 * 
 */
public class DeltaListPersistor {

	public static void save(Collection<IIndexDelta> deltas, Document doc,
			Element indexRoot) {
		final Element listNode = createElement(doc, DELTA_LIST);

		for (IIndexDelta delta : deltas) {

			final Element deltaNode = createElement(doc, DELTA);
			DeltaPersistor.save(delta, doc, deltaNode);

			listNode.appendChild(deltaNode);
		}
		indexRoot.appendChild(listNode);
	}

	public static void restore(Element indexRoot,
			Collection<IIndexDelta> deltaList) throws PersistenceException {
		assertName(indexRoot, INDEX_ROOT);
		
		final NodeList listNodes = getElementsByTagName(indexRoot,
				DELTA_LIST, 1);
		final Element listNode = (Element) listNodes.item(0);

		final NodeList deltaNodes = getElementsByTagName(listNode, DELTA);

		for (int i = 0; i < deltaNodes.getLength(); i++) {
			final Element deltaNode = (Element) deltaNodes.item(i);

			final IIndexDelta delta = DeltaPersistor.getDelta(deltaNode);
			deltaList.add(delta);
		}

	}

}
