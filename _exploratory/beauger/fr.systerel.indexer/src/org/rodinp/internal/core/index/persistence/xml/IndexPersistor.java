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

import org.rodinp.core.index.IDeclaration;
import org.rodinp.internal.core.index.Descriptor;
import org.rodinp.internal.core.index.tables.RodinIndex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Beauger
 * 
 */
public class IndexPersistor {

	public static void restore(Element indexNode, RodinIndex index) {
		final NodeList descNodes = getElementsByTagName(indexNode, DESCRIPTOR);

		for (int i = 0; i < descNodes.getLength(); i++) {
			final Element descNode = (Element) descNodes.item(i);
			final IDeclaration declaration = DescPersistor.getDeclaration(descNode);
			final Descriptor desc = index.makeDescriptor(declaration);
			DescPersistor.addOccurrences(descNode, desc);
		}
	}

	public static void save(RodinIndex index, Document doc, Element indexNode) {
		for (Descriptor desc : index.getDescriptors()) {
			final Element descNode = createElement(doc, DESCRIPTOR);
			DescPersistor.save(desc, doc, descNode);
			indexNode.appendChild(descNode);
		}
	}

}
