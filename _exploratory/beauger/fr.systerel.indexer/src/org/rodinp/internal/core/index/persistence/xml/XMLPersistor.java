/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.index.persistence.xml;

import static org.rodinp.internal.core.index.persistence.xml.XMLElementTypes.*;
import static org.rodinp.internal.core.index.persistence.xml.XMLUtils.*;

import java.io.File;

import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.PerProjectPIM;
import org.rodinp.internal.core.index.persistence.IPersistor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Nicolas Beauger
 * 
 */
public class XMLPersistor implements IPersistor {


	public void restore(File file, PerProjectPIM pppim) {
		if (IndexManager.VERBOSE) {
			System.out.println("restoring from file: " + file.getAbsolutePath());
		}
		try {
			final Element indexRoot = getRoot(file);
			final PPPIMPersistor persistor = new PPPIMPersistor();
			persistor.restore(indexRoot, pppim);

		} catch (Exception e) {
			// TODO forgot everything => ?
			pppim.clear();
			e.printStackTrace();
		}

	}

	public void save(PerProjectPIM pppim, File file) {
		Document doc;
		try {
			doc = getDocument();

			Element indexRoot = createElement(doc, INDEX_ROOT);
			final PPPIMPersistor persistor = new PPPIMPersistor();
			persistor.save(pppim, doc, indexRoot);
			doc.appendChild(indexRoot);

			final String xml = serializeDocument(doc);
			write(file, xml);
		} catch (Exception e) {
			// TODO log
			e.printStackTrace();
		}
	}

}
