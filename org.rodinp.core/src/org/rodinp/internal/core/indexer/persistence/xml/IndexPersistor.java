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

import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.internal.core.indexer.Descriptor;
import org.rodinp.internal.core.indexer.persistence.PersistenceException;
import org.rodinp.internal.core.indexer.tables.RodinIndex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Beauger
 * 
 */
public class IndexPersistor {

	public static void restore(Element indexNode, RodinIndex index)
			throws PersistenceException {
		final NodeList descNodes = getElementsByTagName(indexNode, DESCRIPTOR);

		for (int i = 0; i < descNodes.getLength(); i++) {
			final Element descNode = (Element) descNodes.item(i);
			final IDeclaration declaration =
					DescPersistor.getDeclaration(descNode);
			final Descriptor desc = index.makeDescriptor(declaration);
			DescPersistor.addOccurrences(descNode, desc);
		}
	}

	public static void save(Descriptor[] descriptors, Document doc, Element indexNode) {
		for (Descriptor desc : descriptors) {
			final Element descNode = createElement(doc, DESCRIPTOR);
			DescPersistor.save(desc, doc, descNode);
			indexNode.appendChild(descNode);
		}
	}

}
