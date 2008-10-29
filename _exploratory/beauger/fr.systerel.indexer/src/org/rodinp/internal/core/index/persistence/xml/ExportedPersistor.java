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

import java.util.Set;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.internal.core.index.persistence.PersistenceException;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Beauger
 * 
 */
public class ExportedPersistor {

	public static void appendExported(final Set<IDeclaration> set,
			Document doc, final Element exportNode) {
		for (IDeclaration declaration : set) {
			final Element exportedNode = createElement(doc, EXPORTED);
			DeclPersistor.save(declaration, doc, exportedNode);
			exportNode.appendChild(exportedNode);
		}
	}

	/**
	 * @param exportNode
	 * @param exportTable
	 * @param file
	 * @throws PersistenceException 
	 */
	public static void addExported(Element exportNode, ExportTable exportTable,
			IRodinFile file) throws PersistenceException {

		final NodeList exportedNodes =
				getElementsByTagName(exportNode, EXPORTED);
		for (int i = 0; i < exportedNodes.getLength(); i++) {
			final Element exportedNode = (Element) exportedNodes.item(i);
			final IDeclaration declaration =
					DeclPersistor.getDeclaration(exportedNode);
			exportTable.add(file, declaration);
		}
	}

}
