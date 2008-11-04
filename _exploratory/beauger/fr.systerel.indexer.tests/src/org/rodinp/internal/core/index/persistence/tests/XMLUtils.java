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
package org.rodinp.internal.core.index.persistence.tests;

import static junit.framework.Assert.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author Nicolas Beauger
 * 
 */
public class XMLUtils {

	// TODO core classes will need this stuff

	/**
	 * the index node name for an index element
	 */
	public static final String INDEX_ROOT = "index";

	// copied from DebugUIPlugin

	/**
	 * Returns a Document that can be used to build a DOM tree
	 * 
	 * @return the Document
	 * @throws ParserConfigurationException
	 *             if an exception occurs creating the document builder
	 */
	public static Document getDocument() throws ParserConfigurationException {
		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		return doc;
	}

	/**
	 * Serializes a XML document into a string - encoded in UTF8 format, with
	 * platform line separators.
	 * 
	 * @param doc
	 *            document to serialize
	 * @return the document as a string
	 * @throws TransformerException
	 *             if an unrecoverable error occurs during the serialization
	 * @throws IOException
	 *             if the encoding attempted to be used is not supported
	 */
	public static String serializeDocument(Document doc)
			throws TransformerException, IOException {
		ByteArrayOutputStream s = new ByteArrayOutputStream();

		TransformerFactory factory = TransformerFactory.newInstance();

		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$

		DOMSource source = new DOMSource(doc);
		StreamResult outputTarget = new StreamResult(s);
		transformer.transform(source, outputTarget);

		return s.toString("UTF8"); //$NON-NLS-1$			
	}

	public static Element getRoot(File file) throws Exception {
		Element rootElement = null;
		// Parse the history file
		final InputStream stream =
				new BufferedInputStream(new FileInputStream(file));
		try {
			final DocumentBuilder parser =
					DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document parsedDoc = parser.parse(new InputSource(stream));
			rootElement = parsedDoc.getDocumentElement();
		} finally {
			stream.close();
		}
		return rootElement;
	}

	private static void assertNode(Node expNode, Node actNode) {
		System.out.println("actual node = " + actNode.getNodeName());

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
		final String expSer =
				serializeDocument(getRoot(expected).getOwnerDocument());
		final String actSer =
				serializeDocument(getRoot(actual).getOwnerDocument());
		System.out.println("expected file:\n" + expSer);
		System.out.println("actual file:\n" + actSer);
		if (actSer.equals(expSer)) {
			System.out.println("Serialized equal");
		} else {
			System.out.println("compare: " + actSer.compareTo(expSer));
		}

		assertNode(getRoot(expected), getRoot(actual));

	}

}
