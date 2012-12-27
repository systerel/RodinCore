/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.util.Util;

/**
 * Helper class for accessing to the DOM implementation in the Java library.
 * 
 * @author Laurent Voisin
 */
public class DOMProvider {

	private static final DOMProvider INSTANCE = new DOMProvider();
	
	/**
	 * Returns the unique instance of this class.
	 * 
	 * @return the unique instance of this class
	 */
	public static DOMProvider getInstance() {
		return INSTANCE;
	}
	
	private ThreadLocal<DocumentBuilder> builder =
		new ThreadLocal<DocumentBuilder>();
	
	// Locked by this
	private DocumentBuilderFactory builderFactory;
	
	private ThreadLocal<Transformer> transformer =
		new ThreadLocal<Transformer>();
	
	// Locked by this
	private TransformerFactory transformerFactory;
	
	private DOMProvider() {
		// singleton class
	}
	
	private synchronized DocumentBuilder createDocumentBuilder()
			throws RodinDBException {

		if (builderFactory == null) {
			try {
				builderFactory = DocumentBuilderFactory.newInstance();
				builderFactory.setCoalescing(true);
				builderFactory.setExpandEntityReferences(true);
				builderFactory.setIgnoringComments(true);
				builderFactory.setIgnoringElementContentWhitespace(false);
				builderFactory.setNamespaceAware(true);
				builderFactory.setSchema(null);
				builderFactory.setValidating(false);
				builderFactory.setXIncludeAware(false);
			} catch (FactoryConfigurationError e) {
				Util.log(e, "Can't get a DOM builder");
				throw new RodinDBException(e,
						IRodinDBStatusConstants.XML_CONFIG_ERROR);
			}
		}
		try {
			return builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Util.log(e, "Can't get a DOM builder");
			throw new RodinDBException(e,
					IRodinDBStatusConstants.XML_CONFIG_ERROR);
		}
	}
	
	private synchronized Transformer createDOMTransformer()
			throws RodinDBException {
		
		if (transformerFactory == null) {
			try {
				transformerFactory = TransformerFactory.newInstance();
			} catch (TransformerFactoryConfigurationError e) {
				Util.log(e, "Can't get a DOM transformer");
				throw new RodinDBException(e,
						IRodinDBStatusConstants.XML_CONFIG_ERROR);
			}
		}
		try {
			Transformer result = transformerFactory.newTransformer();
			result.setOutputProperty(OutputKeys.INDENT, "yes");
			result.setOutputProperty(OutputKeys.METHOD, "xml");
			result.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
			return result;
		} catch (TransformerConfigurationException e) {
			Util.log(e, "Can't get a DOM transformer");
			throw new RodinDBException(e,
					IRodinDBStatusConstants.XML_CONFIG_ERROR);
		}
	}

	/**
	 * Returns the DOM builder for the current thread.  The result of this
	 * method call must be kept confined in the current thread.
	 * 
	 * @return a DOM document builder for the current thread
	 * @throws RodinDBException in case of XML configuration error
	 */
	public DocumentBuilder getDocumentBuilder() throws RodinDBException {
		DocumentBuilder result = builder.get();
		if (result == null) {
			result = createDocumentBuilder();
			builder.set(result);
		}
		return result;
	}
	
	public Transformer getDOMTransformer() throws RodinDBException {
		Transformer result = transformer.get();
		if (result == null) {
			result = createDOMTransformer();
			transformer.set(result);
		}
		return result;
	}

}
