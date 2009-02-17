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

import static org.rodinp.internal.core.indexer.persistence.xml.XMLAttributeTypes.*;
import static org.rodinp.internal.core.indexer.persistence.xml.XMLElementTypes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rodinp.core.IRodinFile;
import org.rodinp.internal.core.indexer.persistence.PersistenceException;
import org.rodinp.internal.core.indexer.persistence.PersistentTotalOrder;
import org.rodinp.internal.core.indexer.sort.Node;
import org.rodinp.internal.core.indexer.sort.TotalOrder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Beauger
 * 
 */
public class TotalOrderPersistor {

	public static void restore(Element orderNode,
			TotalOrder<IRodinFile> totalOrder) throws PersistenceException {

		final String isSortedStr = getAttribute(orderNode, IS_SORTED);
		final boolean isSorted = Boolean.parseBoolean(isSortedStr);

		final NodeList nodeNodes = getElementsByTagName(orderNode, NODE);
		final List<Node<IRodinFile>> fileNodes =
				new ArrayList<Node<IRodinFile>>();
		final Map<IRodinFile, List<IRodinFile>> predMap =
				new HashMap<IRodinFile, List<IRodinFile>>();

		for (int i = 0; i < nodeNodes.getLength(); i++) {
			final Element nodeNode = (Element) nodeNodes.item(i);
			final Node<IRodinFile> fileNode =
					NodePersistor.restoreIRFNode(nodeNode, predMap);
			fileNodes.add(fileNode);
		}

		final NodeList iterNodes = getElementsByTagName(orderNode, ITERATED);
		final List<IRodinFile> iterated =
				FileNodeListPersistor.restore(iterNodes, LABEL);

		final PersistentTotalOrder<IRodinFile> pto =
				new PersistentTotalOrder<IRodinFile>(isSorted, fileNodes,
						iterated);

		totalOrder.setPersistentData(pto, predMap);
	}

	public static void save(PersistentTotalOrder<IRodinFile> order, Document doc,
			Element orderNode) {

		final String isSortedStr = Boolean.toString(order.isSorted());
		setAttribute(orderNode, IS_SORTED, isSortedStr);

		for (Node<IRodinFile> node : order.getNodes()) {
			final Element nodeNode = createElement(doc, NODE);
			NodePersistor.save(node, doc, nodeNode);
			orderNode.appendChild(nodeNode);
		}

		FileNodeListPersistor.saveFiles(order.getIterated(), doc,
				orderNode, ITERATED, LABEL);
	}

}
