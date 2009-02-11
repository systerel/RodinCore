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

import static org.rodinp.internal.core.indexer.persistence.xml.IREPersistor.*;
import static org.rodinp.internal.core.indexer.persistence.xml.XMLAttributeTypes.*;
import static org.rodinp.internal.core.indexer.persistence.xml.XMLElementTypes.*;

import java.util.Set;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.internal.core.indexer.persistence.PersistenceException;
import org.rodinp.internal.core.indexer.tables.ExportTable;
import org.rodinp.internal.core.indexer.tables.IExportTable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Beauger
 * 
 */
public class ExpTablePersistor {

	public static void restore(Element expTableNode, ExportTable exportTable)
			throws PersistenceException {
		final NodeList exportNodes = getElementsByTagName(expTableNode, EXPORT);
		for (int i = 0; i < exportNodes.getLength(); i++) {
			final Element exportNode = (Element) exportNodes.item(i);
			final IRodinFile file = getFileAtt(exportNode);
			ExportedPersistor.addExported(exportNode, exportTable, file);
		}
	}

	public static void save(IExportTable exportTable, Document doc,
			Element exportTableNode) {
		for (IRodinFile file : exportTable.files()) {
			final Element exportNode = createElement(doc, EXPORT);

			setFileAtt(file, exportNode);

			final Set<IDeclaration> exported = exportTable.get(file);
			ExportedPersistor.appendExported(exported, doc, exportNode);

			exportTableNode.appendChild(exportNode);
		}
	}

	private static IRodinFile getFileAtt(Element fileNode)
			throws PersistenceException {
		return getIRFAtt(fileNode, FILE);
	}

	private static void setFileAtt(IRodinFile file, Element fileElem) {
		IREPersistor.setIREAtt(file, FILE, fileElem);
	}

}
