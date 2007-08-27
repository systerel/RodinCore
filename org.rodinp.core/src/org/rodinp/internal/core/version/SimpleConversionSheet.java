/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.version;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class SimpleConversionSheet extends ConversionSheet {

	private final Conversion[] conversions;
	
	private Transformer transformer;
	
	// TODO put this somewhere else!
	static {
		assert TransformerFactory.newInstance().getFeature(DOMSource.FEATURE);
	}
	
	public SimpleConversionSheet(IConfigurationElement configElement, IFileElementType<IRodinFile> type) {
		super(configElement, type);
		IConfigurationElement[] confElements = configElement.getChildren("element");
		conversions = new Conversion[confElements.length];
		final List<String> paths = new ArrayList<String>(confElements.length);
		for (int i=0; i<confElements.length; i++) {
			conversions[i] = new Conversion(confElements[i], this);
			String path = conversions[i].getPath();
			if (paths.contains(path)) {
				throw new IllegalStateException(
						"Paths of different groups of operations must be distinct");
			} else {
				paths.add(path);
			}
		}		
	}

	@Override
	public Transformer getTransformer() throws RodinDBException {
		if (transformer == null) {
			String convDoc = computeSheet();
			final Source source = new StreamSource(new StringReader(convDoc));
			TransformerFactory factory = TransformerFactory.newInstance();
			try {
				transformer = factory.newTransformer(source);
			} catch (TransformerConfigurationException e) {
				throw new RodinDBException(e, IRodinDBStatusConstants.CONVERSION_ERROR);
			}
		}
		return transformer;
	}
	
	private static String T7 = 
		XSLConstants.XML + "\n<" + XSLConstants.XSL_TRANSFORM + " " +
		XSLConstants.XSL_VERSION + "=\"" + XSLConstants.XSL_VERSION_VAL + "\" " +
		XSLConstants.XSL_XMLNS + "=\"" + XSLConstants.XSL_XMLNS_URI + "\">\n<" +
		XSLConstants.XSL_OUTPUT + " " + XSLConstants.XSL_ENCODING + "=\"" + XSLConstants.XSL_UTF8 + "\" " +
		XSLConstants.XSL_INDENT + "=\"" + XSLConstants.XSL_YES + "\"/>\n";
	private static String T8 = "</" + XSLConstants.XSL_TRANSFORM + ">";
	
	private String computeSheet() {
		
		StringBuffer sheet = new StringBuffer();
		
		sheet.append(T7);
				
		getPrefix(sheet);
		
		for (Conversion conversion : conversions) {
			conversion.addTemplates(sheet);
		}
		
		addPostfix(sheet);
		
		sheet.append(T8);
		
		return sheet.toString();
		
	}
	
	@Override
	public String toString() {
		return computeSheet();
	}
	
	private static String T1 = 
		"<" + XSLConstants.XSL_TEMPLATE + " " + XSLConstants.XSL_MATCH + "=\"";
	private static String T2 = "\">\n\t<" + XSLConstants.XSL_COPY + ">\n";
	private static String T9 = 
		"\t\t<" + XSLConstants.XSL_APPLY_TEMPLATES + " " + XSLConstants.XSL_SELECT + "=\"" + 
		XSLConstants.XSL_ALL;
	private static String T3 = "\">\n";
	private static String T4 = "\t\t</" + XSLConstants.XSL_APPLY_TEMPLATES + ">\n";
	private static String T5 = "\"/>\n";
	private static String T6 = "\t</" + XSLConstants.XSL_COPY + ">\n</" + XSLConstants.XSL_TEMPLATE + ">\n";
	private static String TA = 
		"\t<" + XSLConstants.XSL_ATTRIBUTE + " " + XSLConstants.XSL_NAME + "=\"version\">";
	private static String TB = "</" + XSLConstants.XSL_ATTRIBUTE + ">\n";
		
	protected void createCopyTemplate(StringBuffer document, String match) {
		
		document.append(T1);
		document.append(match);
		document.append(T2);
		
		if (match == XSLConstants.XSL_ROOT) {
			document.append("\t");
			document.append(TA);
			document.append(getVersion());
			document.append(TB);
		}
		
		document.append(T9);
		if (hasSorter()) {
			document.append(T3);
			
			addSorter(document);
			
			document.append(T4);
		} else {
			document.append(T5);
		}
		
		document.append(T6);
	}

	public boolean hasSorter() {
		return false;
	}

	public void addSorter(StringBuffer document) {
		// do nothing
	}
	
	private static String TC = "</" + XSLConstants.XSL_TEMPLATE + ">\n";
	
	private void getPrefix(StringBuffer document) {
		
		document.append(T1);
		document.append("/");
		document.append(getType());
		document.append("/@version\">\n");
		document.append(TA);
		document.append(getVersion());
		document.append(TB);
		document.append(TC);
		
		createCopyTemplate(document, XSLConstants.XSL_ROOT);
		
	}
	
	private void addPostfix(StringBuffer document) {
		
		createCopyTemplate(document, XSLConstants.XSL_ALL);
	}

}
