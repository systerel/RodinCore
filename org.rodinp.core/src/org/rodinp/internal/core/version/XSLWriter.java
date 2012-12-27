/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.version;

import static java.util.Collections.singletonList;
import static org.rodinp.internal.core.version.XSLConstants.XSL_ALL;
import static org.rodinp.internal.core.version.XSLConstants.XSL_ALL_ATTRIBUTES;
import static org.rodinp.internal.core.version.XSLConstants.XSL_ALL_NODES;
import static org.rodinp.internal.core.version.XSLConstants.XSL_APPLY_TEMPLATES;
import static org.rodinp.internal.core.version.XSLConstants.XSL_ATTRIBUTE;
import static org.rodinp.internal.core.version.XSLConstants.XSL_COPY;
import static org.rodinp.internal.core.version.XSLConstants.XSL_ELEMENT;
import static org.rodinp.internal.core.version.XSLConstants.XSL_ENCODING;
import static org.rodinp.internal.core.version.XSLConstants.XSL_INDENT;
import static org.rodinp.internal.core.version.XSLConstants.XSL_LANG;
import static org.rodinp.internal.core.version.XSLConstants.XSL_MATCH;
import static org.rodinp.internal.core.version.XSLConstants.XSL_NAME;
import static org.rodinp.internal.core.version.XSLConstants.XSL_ORDER;
import static org.rodinp.internal.core.version.XSLConstants.XSL_OUTPUT;
import static org.rodinp.internal.core.version.XSLConstants.XSL_SELECT;
import static org.rodinp.internal.core.version.XSLConstants.XSL_SORT;
import static org.rodinp.internal.core.version.XSLConstants.XSL_TEMPLATE;
import static org.rodinp.internal.core.version.XSLConstants.XSL_TRANSFORM;
import static org.rodinp.internal.core.version.XSLConstants.XSL_VALUE_OF;
import static org.rodinp.internal.core.version.XSLConstants.XSL_VERSION;
import static org.rodinp.internal.core.version.XSLConstants.XSL_XMLNS;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Nicolas Beauger
 * 
 */
public class XSLWriter {

	private final StringBuilder document = new StringBuilder();
	private int numTabs = 0;
	private boolean addTabs = true;

	private static <T> List<T> mList(T... objs) {
		return Arrays.asList(objs);
	}

	private void quote(String value) {
		document.append("\"");
		document.append(value);
		document.append("\"");
	}

	private void innerAttribute(String name, String value) {
		document.append(" ");
		document.append(name);
		document.append("=");
		quote(value);
	}

	private void commonStartMarkup(String markup, List<String> attributes,
			List<String> values) {
		assert attributes.size() == values.size();
		appendTabs();
		document.append("<" + markup);
		for (int i = 0; i < attributes.size(); i++) {
			innerAttribute(attributes.get(i), values.get(i));
		}
	}

	public void beginMarkup(String markup) {
		final List<String> emptyList = Collections.emptyList();
		beginMarkup(markup, emptyList, emptyList, true);
	}

	public void simpleMarkup(String markup, List<String> attributes,
			List<String> values) {
		simpleMarkup(markup, attributes, values, true);
	}
	
	public void simpleMarkup(String markup, List<String> attributes,
			List<String> values, boolean newLine) {
		commonStartMarkup(markup, attributes, values);
		document.append("/>");
		if (newLine) {
			document.append("\n");
		} else {
			addTabs = false;
		}
	}

	public void beginMarkup(String markup, List<String> attributes,
			List<String> values, boolean newLine) {
		commonStartMarkup(markup, attributes, values);
		document.append(">");
		if (newLine) {
			document.append("\n");
		} else {
			addTabs = false;
		}
		numTabs++;
	}

	public void endMarkup(String markup) {
		numTabs--;
		appendTabs();
		document.append("</" + markup + ">\n");
		addTabs = true;
	}

	public void appendTabs() {
		if (addTabs) {
			for (int i = 0; i < numTabs; i++) {
				document.append("\t");
			}
		}
	}

	public void simpleTemplate(String match) {
		simpleMarkup(XSL_TEMPLATE, singletonList(XSL_MATCH),
				singletonList(match));
	}

	public void beginTemplate(String match) {
		beginMarkup(XSL_TEMPLATE, singletonList(XSL_MATCH),
				singletonList(match), true);
	}

	public void endTemplate() {
		endMarkup(XSL_TEMPLATE);
	}

	public void beginCopy() {
		beginMarkup(XSL_COPY);
	}

	public void endCopy() {
		endMarkup(XSL_COPY);
	}

	public void simpleApplyTemplates(String select) {
		simpleMarkup(XSL_APPLY_TEMPLATES, mList(XSL_SELECT), mList(select));
	}

	public void beginApplyTemplates(String select) {
		beginMarkup(XSL_APPLY_TEMPLATES, mList(XSL_SELECT), mList(select), true);
	}

	public void endApplyTemplates() {
		endMarkup(XSL_APPLY_TEMPLATES);
	}

	public void simpleAttribute(String name, String value) {
		beginMarkup(XSL_ATTRIBUTE, mList(XSL_NAME), mList(name), false);
		document.append(value);
		endMarkup(XSL_ATTRIBUTE);
	}

	public void beginElement(String name) {
		beginMarkup(XSL_ELEMENT, mList(XSL_NAME), mList(name), true);
	}

	public void endElement() {
		endMarkup(XSL_ELEMENT);
	}

	public void beginAttribute(String name) {
		beginMarkup(XSL_ATTRIBUTE, mList(XSL_NAME), mList(name), true);
	}

	public void endAttribute() {
		endMarkup(XSL_ATTRIBUTE);
	}
	
	public void valueOf(String select) {
		valueOf(select, true);
	}
	
	public void valueOf(String select, boolean newLine) {
		simpleMarkup(XSL_VALUE_OF, mList(XSL_SELECT), mList(select), newLine);
	}

	public void xmlHeader() {
		document.append(XSLConstants.XML + "\n");
	}

	public void beginTransform(String version, String xmlns, String encoding,
			String indent) {
		beginMarkup(XSL_TRANSFORM, mList(XSL_VERSION, XSL_XMLNS), mList(
				version, xmlns), true);
		simpleMarkup(XSL_OUTPUT, mList(XSL_ENCODING, XSL_INDENT), mList(
				encoding, indent));
	}

	public void endTransform() {
		endMarkup(XSL_TRANSFORM);
	}

	public void sort(String lang, String select, String order) {
		simpleMarkup(XSL_SORT, mList(XSL_LANG, XSL_SELECT, XSL_ORDER), mList(
				lang, select, order));
	}

	private void appendApplyTemplates(String select, SimpleConversionSheet sheet) {
		if (sheet.hasSorter()) {
			beginApplyTemplates(select);
			sheet.addSorter(this);
			endApplyTemplates();
		} else {
			simpleApplyTemplates(select);
		}
	}

	public void appendApplyTemplates(SimpleConversionSheet sheet) {
		if (sheet.hasSorter()) {
			appendApplyTemplates(XSL_ALL_ATTRIBUTES, sheet);
			appendApplyTemplates(XSL_ALL_NODES, sheet);
		} else {
			appendApplyTemplates(XSL_ALL, sheet);
		}
	}

	public void appendCopyAllTemplate(SimpleConversionSheet sheet) {
		beginTemplate(XSL_ALL);
		beginCopy();

		appendApplyTemplates(sheet);

		endCopy();
		endTemplate();
	}

	public void appendCopyAllTemplate() {
		beginTemplate(XSL_ALL);
		beginCopy();

		simpleApplyTemplates(XSL_ALL);

		endCopy();
		endTemplate();
	}

	
	
	public void appendSource(String source) {
		document.append(source);
		
	}

	public String getDocument() {
		return document.toString();
	}
}
