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

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.rodinp.internal.core.index.PerProjectPIM;
import org.rodinp.internal.core.index.persistence.IPersistor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Nicolas Beauger
 * 
 */
public class XMLPersistor implements IPersistor {

	// TODO use schemas ?
	private void testSchemas() throws Exception {
		File schemaFile = null;
		File xmlFile = null;

		final SchemaFactory xmlSchemaFactory =
				SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Schema schema = xmlSchemaFactory.newSchema(schemaFile);
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setSchema(schema);
		
		dbf.setNamespaceAware(true);
		final DocumentBuilder parser = dbf.newDocumentBuilder();
//		parser.setErrorHandler(eh);
		parser.parse(xmlFile);
	}

	// TODO use namespaces
	// TODO check casts
	// TODO check length when exactly 1 child node is expected 

	public void restore(File file, PerProjectPIM pppim) {
		// dummyRestore(file);
		System.out.println("restoring from file: " + file.getAbsolutePath());
		try {
			final Element indexRoot = getRoot(file);
//			final NodeList indexRoots = getElementsByTagName(root, INDEX_ROOT);
//			// TODO assert length == 1
//			final Element indexRoot = (Element) indexRoots.item(0);
			// TODO check cast
			final PPPIMPersistor persistor = new PPPIMPersistor();
			persistor.restore(indexRoot, pppim);

		} catch (Exception e) {
			// TODO forgot everything => ?
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
			// TODO Throw my own PersistenceException with encapsulated cause e
			e.printStackTrace();
		}
	}

}
