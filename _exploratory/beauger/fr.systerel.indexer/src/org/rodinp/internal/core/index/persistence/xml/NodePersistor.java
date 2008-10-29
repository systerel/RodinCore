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

import org.rodinp.core.IRodinFile;
import org.rodinp.internal.core.index.tables.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Nicolas Beauger
 * 
 */
public class NodePersistor {

	public static Node<IRodinFile> getIRFNode(Element nodeNode) {
		final IRodinFile label =
				(IRodinFile) IREPersistor.getIREAtt(LABEL, nodeNode);
		final String markAtt = getAttribute(nodeNode, MARK);//, Boolean.toString(node.isMarked()));
		final boolean mark = Boolean.parseBoolean(markAtt);
		final String orderPosAtt = getAttribute(nodeNode, ORDER_POS);
		final int orderPos = Integer.parseInt(orderPosAtt);

		final Node<IRodinFile> result = new Node<IRodinFile>(label);
		result.setMark(mark);
		result.setOrderPos(orderPos);
		
		return result;
	}

	public static void save(Node<IRodinFile> node, Document doc,
			Element nodeNode) {
		IREPersistor.setIREAtt(node.getLabel(), LABEL, nodeNode);
		setAttribute(nodeNode, MARK, Boolean.toString(node.isMarked()));
		setAttribute(nodeNode, ORDER_POS, Integer.toString(node.getOrderPos()));

		IRFNodeListPersistor.saveFilesInNodes(node.getPredecessors(), doc, nodeNode,
				PREDECESSOR);

		IRFNodeListPersistor.saveFilesInNodes(node.getSuccessors(), doc, nodeNode,
				SUCCESSOR);
	}
}
