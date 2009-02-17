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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.rodinp.core.IRodinFile;
import org.rodinp.internal.core.indexer.persistence.PersistenceException;
import org.rodinp.internal.core.indexer.sort.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Beauger
 * 
 */
public class FileNodeListPersistor {

	public static List<IRodinFile> restore(NodeList nodeNodes,
			XMLAttributeTypes attType) throws PersistenceException {
		final List<IRodinFile> fileNodes = new ArrayList<IRodinFile>();
		for (int i = 0; i < nodeNodes.getLength(); i++) {
			final Element nodeNode = (Element) nodeNodes.item(i);
			final IRodinFile file = IREPersistor.getIRFAtt(nodeNode, attType);
			fileNodes.add(file);
		}
		return fileNodes;
	}

	public static void saveFiles(Collection<IRodinFile> files, Document doc,
			Element parent, XMLElementTypes nodeType, XMLAttributeTypes attType) {
		for (IRodinFile file : files) {
			saveFile(file, doc, parent, nodeType, attType);
		}
	}

	private static void saveFile(IRodinFile file, Document doc, Element parent,
			XMLElementTypes nodeType, XMLAttributeTypes attType) {
		final Element nodeNode = createElement(doc, nodeType);
		IREPersistor.setIREAtt(file, attType, nodeNode);
		parent.appendChild(nodeNode);
	}

	public static void saveFilesInNodes(List<Node<IRodinFile>> filesInNodes,
			Document doc, Element nodeNode, XMLElementTypes nodeType,
			XMLAttributeTypes attType) {
		for (Node<IRodinFile> node : filesInNodes) {
			saveFile(node.getLabel(), doc, nodeNode, nodeType, attType);
		}
	}
}
