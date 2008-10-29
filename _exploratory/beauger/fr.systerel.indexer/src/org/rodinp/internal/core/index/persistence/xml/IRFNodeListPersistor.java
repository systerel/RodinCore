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

import static org.rodinp.internal.core.index.persistence.xml.XMLAttributeTypes.*;
import static org.rodinp.internal.core.index.persistence.xml.XMLElementTypes.*;

import java.util.ArrayList;
import java.util.List;

import org.rodinp.core.IRodinFile;
import org.rodinp.internal.core.index.tables.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Beauger
 * 
 */
public class IRFNodeListPersistor {

	public static List<IRodinFile> restore(NodeList nodeNodes) {
		final List<IRodinFile> fileNodes = new ArrayList<IRodinFile>();
		for (int i = 0; i < nodeNodes.getLength(); i++) {
			final Element nodeNode = (Element) nodeNodes.item(i);
			final IRodinFile file =
					(IRodinFile) IREPersistor.getIREAtt(FILE, nodeNode);
			fileNodes.add(file);
		}
		return fileNodes;
	}

	public static void saveFiles(List<IRodinFile> files, Document doc,
			Element parent, XMLElementTypes nodeType) {
		for (IRodinFile file : files) {
			saveFile(file, doc, parent, nodeType);
		}
	}

	private static void saveFile(IRodinFile file, Document doc, Element parent,
			XMLElementTypes nodeType) {
		final Element nodeNode = createElement(doc, nodeType);
		IREPersistor.setIREAtt(file, FILE, nodeNode);
		parent.appendChild(nodeNode);
	}

	public static void saveFilesInNodes(List<Node<IRodinFile>> filesInNodes, Document doc,
			Element nodeNode, XMLElementTypes nodeType) {
		for (Node<IRodinFile> node : filesInNodes) {
			saveFile(node.getLabel(), doc, nodeNode, nodeType);
		}
	}
}
