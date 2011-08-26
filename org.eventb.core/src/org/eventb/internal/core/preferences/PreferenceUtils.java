/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.preferences;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility class for preferences using.
 */
public class PreferenceUtils {

	/**
	 * The debug flag. This is set by the option when the platform is launched.
	 * Client should not try to reset this flag.
	 */
	public static boolean DEBUG = false;

	public static class PreferenceException extends RuntimeException {

		private static final long serialVersionUID = -4388540765121161963L;

		private static final PreferenceException INSTANCE = new PreferenceException();

		private PreferenceException() {
			// singleton
		}

		public static PreferenceException getInstance() {
			return INSTANCE;
		}
	}

	public static class ReadPrefMapEntry<T> implements IPrefMapEntry<T> {

		private final String key;
		private final T value;
		
		public ReadPrefMapEntry(String key, T value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public T getValue() {
			return value;
		}

		@Override
		public void setKey(String key) {
			// do nothing
		}

		@Override
		public void setValue(T value) {
			// do nothing
		}

		@Override
		public T getReference() {
			return null;
		}
		
	}
	
	public static class UnresolvedPrefMapEntry<T> extends ReadPrefMapEntry<T> {

		public UnresolvedPrefMapEntry(String key) {
			super(key, null);
		}
		
	}
	
	/**
	 * Returns a string representation of a list of input objects. The objects
	 * are separated by a given character.
	 * 
	 * @param objects
	 *            a list of objects
	 * @param separator
	 *            the character to use to separate the objects
	 * @return the string representation of input objects
	 */
	public static <T> String flatten(List<T> objects, String separator) {
		final StringBuffer buffer = new StringBuffer();
		boolean first = true;
		for (T item : objects) {
			if (first) {
				first = false;
			} else {
				buffer.append(separator);
			}
			buffer.append(item);
		}
		return buffer.toString();
	}

	/**
	 * Parse a character separated string to a list of string.
	 * 
	 * @param stringList
	 *            the comma separated string.
	 * @param c
	 *            the character separates the string
	 * @return an array of strings that make up the character separated input
	 *         string.
	 */
	public static String[] parseString(String stringList, String c) {
		StringTokenizer st = new StringTokenizer(stringList, c);//$NON-NLS-1$
		ArrayList<String> result = new ArrayList<String>();
		while (st.hasMoreElements()) {
			result.add((String) st.nextElement());
		}
		return result.toArray(new String[result.size()]);
	}

	// for compatibility
	public static ITacticDescriptor loopOnAllPending(
			List<ITacticDescriptor> descs, String id) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor comb = reg
				.getCombinatorDescriptor(AutoTactics.LoopOnAllPending.COMBINATOR_ID);
		return comb.instantiate(descs, id);
	}

	public static enum XMLElementTypes {
		TACTIC_PREF, PREF_UNIT, SIMPLE, PARAMETERIZED, PARAMETER, COMBINED, PREF_REF;
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}

		public static Element createElement(Document doc, XMLElementTypes name) {
			return doc.createElement(name.toString());
		}

		public static boolean hasName(Node node, XMLElementTypes name) {
			return node.getNodeName().equals(name.toString());
		}

		public static NodeList getElementsByTagName(Element node,
				XMLElementTypes nodeType) {
			return node.getElementsByTagName(nodeType.toString());
		}

		public static void assertName(Node node, XMLElementTypes name)
				throws PreferenceException {
			if (!hasName(node, name)) {
				throw PreferenceException.getInstance();
			}
		}
	}

	public static enum XMLAttributeTypes {
		PREF_KEY, TACTIC_ID, PARAMETERIZER_ID, LABEL, TYPE, COMBINATOR_ID;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}

		public static String getAttribute(Node node,
				XMLAttributeTypes attributeType) throws PreferenceException {
			final NamedNodeMap attributes = node.getAttributes();
			final Node att = attributes.getNamedItem(attributeType.toString());
			if (att == null) {
				throw PreferenceException.getInstance();
			}
			return att.getNodeValue();
		}

		public static void setAttribute(Element node,
				XMLAttributeTypes attributeType, String value) {
			node.setAttribute(attributeType.toString(), value);
		}
	}

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
	 * Makes a DOM document from the given string.
	 * 
	 * @param str
	 *            xml content
	 * @return a document
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document makeDocument(String str)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
		return docBuilder.parse(new InputSource(new StringReader(str)));
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
	
	public static Node getUniqueChild(Node node) {
		final NodeList unitChildren = node.getChildNodes();
		for (int j = 0; j < unitChildren.getLength(); j++) {
			final Node child = unitChildren.item(j);
			if (child instanceof Element) {
				return child;
			}
		}
		throw PreferenceException.getInstance();
	}

}
