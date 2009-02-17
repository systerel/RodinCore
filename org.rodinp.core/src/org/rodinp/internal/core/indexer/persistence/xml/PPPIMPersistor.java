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

import org.rodinp.internal.core.indexer.PerProjectPIM;
import org.rodinp.internal.core.indexer.ProjectIndexManager;
import org.rodinp.internal.core.indexer.persistence.PersistenceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Beauger
 * 
 */
public class PPPIMPersistor {

	public static void restore(Element indexRoot, PerProjectPIM pppim)
			throws PersistenceException {
		assertName(indexRoot, INDEX_ROOT);
		final NodeList pimNodes = getElementsByTagName(indexRoot, PIM);

		for (int i = 0; i < pimNodes.getLength(); i++) {
			final Element pimNode = (Element) pimNodes.item(i);

			final PIMPersistor persistor = new PIMPersistor();
			
			pppim.put(persistor.restore(pimNode));
		}
	}

	public static void save(PerProjectPIM pppim, Document doc, Element indexRoot) {
		for (ProjectIndexManager pim : pppim.pims()) {
			final Element pimNode = createElement(doc, PIM);

			final PIMPersistor persistor = new PIMPersistor();
			persistor.save(pim.getPersistentData(), doc, pimNode);

			indexRoot.appendChild(pimNode);
		}
	}

}
