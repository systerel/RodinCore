/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
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
import static org.rodinp.internal.core.indexer.persistence.xml.XMLUtils.*;

import java.io.File;

import org.rodinp.internal.core.indexer.IndexManager;
import org.rodinp.internal.core.indexer.PerProjectPIM;
import org.rodinp.internal.core.indexer.ProjectIndexManager;
import org.rodinp.internal.core.indexer.persistence.IPersistor;
import org.rodinp.internal.core.indexer.persistence.PersistentIndexManager;
import org.rodinp.internal.core.indexer.persistence.PersistentPIM;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Nicolas Beauger
 * 
 */
public class XMLPersistor implements IPersistor {

	@Override
	public boolean restore(File file, PersistentIndexManager data) {
		if (IndexManager.VERBOSE) {
			System.out
					.println("restoring from file: " + file.getAbsolutePath());
		}
		try {
			final Element indexRoot = getRoot(file);
			if (indexRoot == null) {
				return false;
			}
			PPPIMPersistor.restore(indexRoot, data.getPPPIM());

			DeltaListPersistor.restore(indexRoot, data.getDeltas());

			IndexerRegPersistor.restore(indexRoot, data.getIndexerRegistry());
			
			return true;
		} catch (Exception e) {
			data.clear();

			if (IndexManager.DEBUG) {
				e.printStackTrace();
			}
			return false;
		}
	}

	@Override
	public boolean save(PersistentIndexManager persistIM, File file) {
		try {
			Document doc = getDocument();

			Element indexRoot = createElement(doc, INDEX_ROOT);

			PPPIMPersistor.save(persistIM.getPPPIM(), doc, indexRoot);

			DeltaListPersistor.save(persistIM.getDeltas(), doc, indexRoot);
			
			IndexerRegPersistor.save(persistIM.getIndexerRegistry(), doc,
					indexRoot);
			
			doc.appendChild(indexRoot);

			final String xml = serializeDocument(doc);
			write(file, xml);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean saveProject(PersistentPIM pim, File file) {
		try {
			Document doc = getDocument();

			final Element pimNode = createElement(doc, PIM);

			final PIMPersistor persistor = new PIMPersistor();
			persistor.save(pim, doc, pimNode);

			doc.appendChild(pimNode);

			final String xml = serializeDocument(doc);
			write(file, xml);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean restoreProject(File file, PerProjectPIM pppim) {
		try {
			final Element pimNode = getRoot(file);
			if (pimNode == null) {
				return false;
			}
			final PIMPersistor persistor = new PIMPersistor();
			final ProjectIndexManager pim = persistor.restore(pimNode);
			pppim.put(pim);
			pim.fireUnprocessedFiles();
			return true;
		} catch (Exception e) {
			if (IndexManager.DEBUG) {
				e.printStackTrace();
			}
			return false;
		}
	}

}
