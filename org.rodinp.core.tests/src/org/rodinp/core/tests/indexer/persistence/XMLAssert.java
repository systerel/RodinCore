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
package org.rodinp.core.tests.indexer.persistence;

import static junit.framework.Assert.*;
import static org.rodinp.internal.core.indexer.persistence.xml.XMLUtils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Beauger
 * 
 */
public class XMLAssert {

	private static void assertNode(Node expNode, Node actNode) {

		assertNodeName(expNode, actNode);

		if (expNode.hasAttributes()) {
			assertNodeAttributes((Element) expNode, (Element) actNode);
		}

		removeTextChildNodes(expNode);
		removeTextChildNodes(actNode);
		if (expNode.hasChildNodes()) {
			assertNodeChildNodes(expNode, actNode);
		}
	}

	private static void assertNodeName(Node expNode, Node actNode) {
		final String expName = expNode.getNodeName();
		final String actName = actNode.getNodeName();
		assertEquals("bad name", expName, actName);
	}

	private static void assertNodeAttributes(Element expNode, Element actNode) {
		final NamedNodeMap expAttrs = expNode.getAttributes();

		for (int i = 0; i < expAttrs.getLength(); i++) {

			final Node expAtt = expAttrs.item(i);
			final String expName = expAtt.getNodeName();
			final String actValue = actNode.getAttribute(expName);
			final String expValue = expAtt.getNodeValue();
			assertEquals("bad value for attribute "
					+ expName
					+ " in node "
					+ actNode, expValue, actValue);
		}
	}

	private static void assertNodeChildNodes(Node expNode, Node actNode) {

		final NodeList expCNodes = expNode.getChildNodes();
		final NodeList actCNodes = actNode.getChildNodes();

		final int expCLength = expCNodes.getLength();

		final int actCLength = actCNodes.getLength();
		assertEquals("bad child nodes length", expCLength, actCLength);
		for (int i = 0; i < expCLength; i++) {
			final Node actCNode = actCNodes.item(i);
			if (!actCNode.getNodeName().equals("#text")) {
				assertNode(expCNodes.item(i), actCNode);
			}
		}
	}

	private static void removeTextChildNodes(Node node) {
		final List<Node> toRemove = new ArrayList<Node>();
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			final Node child = childNodes.item(i);
			if (child.getNodeName().equals("#text")) {
				toRemove.add(child);
			}
		}
		for (Node n : toRemove) {
			node.removeChild(n);
		}
	}

	public static void assertFile(File expected, File actual) throws Exception {
		assertNode(getRoot(expected), getRoot(actual));
	}

}
