/*******************************************************************************
 * Copyright (c) 2008 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soton - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.internal.core.version;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IInternalElementType;

/**
 * @author Stefan Hallerstede
 *
 */
public class SourceConversionSheet extends ConversionSheetWithTransformer {
	
	private final String source;

	public SourceConversionSheet(IConfigurationElement configElement,
			IInternalElementType<?> type) {
		super(configElement, type);
		source = configElement.getAttribute("source");
	}
	
	private static String prefix = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<xsl:transform version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n" +
		"<xsl:output encoding=\"UTF-8\" indent=\"yes\"/>\n";
	private static String setVersionPre =
		"<xsl:template match=\"";
	private static String setVersionVer =
		"/@version\">\n" +
		"\t<xsl:attribute name=\"version\">";
	private static String setVersionPost =
		"</xsl:attribute>\n" +
		"</xsl:template>\n";
	private static String addVersionMatch =
		"<xsl:template match=\"/";
	private static String addVersionCopy =
		"\">\n" +
		"\t<xsl:element name=\"";
	private static String addVersionBody =
		"\">\n" +
		"\t\t<xsl:attribute name=\"version\">";
	private static String addVersionEnd =
		"</xsl:attribute>\n" +
		"\t\t<xsl:apply-templates select=\"* | @*\"/>\n" +
		"\t</xsl:element>\n" +
		"</xsl:template>\n";
	private static String suffix =
		"</xsl:transform>";
	private static String copyTemplate =
		"<xsl:template match=\"* | @*\">\n" +
		"\t<xsl:copy>\n" +
		"\t\t<xsl:apply-templates select=\"* | @*\"/>\n" +
		"\t</xsl:copy>\n" +
		"</xsl:template>";
	
	@Override
	protected String computeSheet() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(prefix);
		buffer.append(setVersionPre);
		buffer.append(getType().getId());
		buffer.append(setVersionVer);
		buffer.append(getVersion());
		buffer.append(setVersionPost);
		buffer.append(addVersionMatch);
		buffer.append(getType().getId());
		buffer.append(addVersionCopy);
		buffer.append(getType().getId());
		buffer.append(addVersionBody);
		buffer.append(getVersion());
		buffer.append(addVersionEnd);
		buffer.append(source);
		buffer.append(copyTemplate);
		buffer.append(suffix);
		return buffer.toString();
	}


}
