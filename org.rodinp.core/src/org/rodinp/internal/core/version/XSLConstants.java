/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel   - added XSL_ALL_ATTRIBUTES and XSL_ALL_NODES
 *******************************************************************************/
package org.rodinp.internal.core.version;

/**
 * @author Stefan Hallerstede
 *
 */
public final class XSLConstants {
	
	public static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	public static final String XSL_NS = "xsl:";
	public static final String XSL_XMLNS = "xmlns:xsl";
	public static final String XSL_TRANSFORM = XSL_NS + "transform";
	public static final String XSL_TEMPLATE = XSL_NS + "template";
	public static final String XSL_APPLY_TEMPLATES = XSL_NS + "apply-templates";
	public static final String XSL_COPY = XSL_NS + "copy";
	public static final String XSL_SORT = XSL_NS + "sort";
	public static final String XSL_LANG = "lang";
	public static final String XSL_ORDER = "order";
	public static final String XSL_SELECT = "select";
	public static final String XSL_MATCH = "match";
	public static final String XSL_NAME = "name";
	public static final String XSL_ELEMENT = XSL_NS + "element";
	public static final String XSL_ATTRIBUTE = XSL_NS + "attribute";
	public static final String XSL_ENCODING = "encoding";
	public static final String XSL_VERSION = "version";
	public static final String XSL_INDENT = "indent";
	public static final String XSL_OUTPUT = XSL_NS + "output";
	public static final String XSL_VALUE_OF = XSL_NS + "value-of";
	
	public static final String XSL_ROOT = "/*";
	public static final String XSL_ALL = "* | @*";
	public static final String XSL_ALL_ATTRIBUTES = "@*";
	public static final String XSL_ALL_NODES = "*";
	public static final String XSL_EN = "en";
	public static final String XSL_CURRENT_NAME = "name()";	
	public static final String XSL_UTF8 = "UTF-8";	
	public static final String XSL_YES = "yes";	
	public static final String XSL_XMLNS_URI = "http://www.w3.org/1999/XSL/Transform";
	public static final String XSL_VERSION_VAL = "1.0";

}
