/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.documentModel;

import static fr.systerel.editor.internal.presentation.RodinConfiguration.COMMENT_HEADER_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.KEYWORD_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.LABEL_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.LEFT_PRESENTATION_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.PRESENTATION_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.getAttributeContentType;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.presentation.RodinConfiguration.ContentType;

/**
 * A class holding a string builder and computing regions to be processed as
 * intervals by the text generator.
 */
public class RodinTextStream {

	public static int MIN_LEVEL = 1;
	private static final String COMMENT_HEADER_DELIMITER = "\u203A"; // the comment "›" character
	public static final String TAB = "\t";
	private static final String WHITESPACE = " ";
	private static final Object LINESEPARATOR = System
			.getProperty("line.separator");
	
	private final StringBuilder builder;
	private int level = MIN_LEVEL;
	private List<EditorRegion> regions;
	
	public static String processMulti(boolean multiLine, String alignString,
			boolean addWhiteSpace, String text) {
		if (!multiLine || text == null)
			return text;
		final String regex = "(\r\n)|(\r)|(\n)";
		if (addWhiteSpace)
			return text.replaceAll(regex, "$0" + alignString + WHITESPACE);
		return text.replaceAll(regex, "$0" + alignString);
	}

	public static String deprocessMulti(String align, boolean multiLine,
			boolean tabbed, String text) {
		if (!multiLine)
			return text;
		return deprocessMulti(align, tabbed, text);
	}

	public static String deprocessMulti(String align, boolean addWhitespace,
			String text) {
		final String commonPatternStart = "((\r\n)|(\r)|(\n))(";
		// Tells that it should take into account one (only) matching pattern
		final String commonPatternEnd = "){1}";
		if (addWhitespace) {
			return text.replaceAll(commonPatternStart + align + WHITESPACE
					+ commonPatternEnd, "$1");
		}
		return text.replaceAll(commonPatternStart + align + commonPatternEnd,
				"$1");
	}

	public RodinTextStream() {
		this.builder = new StringBuilder();
		this.regions = new ArrayList<EditorRegion>();
	}

	protected void addElementRegion(String text, ILElement element,
			ContentType contentType, IAttributeManipulation manipulation,
			boolean multiLine, String alignmentStr) {
		final int start = builder.length();
		final EditorRegion region = getElementRegion(start, getLevel(), text,
				element, contentType, manipulation, multiLine, alignmentStr);
		builder.append(region.getText());
		regions.add(region);
	}

	public EditorRegion getElementRegion(int startOffset, int level,
			String elementText, ILElement element, ContentType contentType,
			IAttributeManipulation manipulation, boolean multiline,
			String alignmentStr) {
		return new EditorRegion(startOffset, level, elementText, element,
				contentType, manipulation, multiline, alignmentStr);
	}

	protected void addAttributeRegion(String text, ILElement element,
			IAttributeManipulation manipulation, IAttributeType attributeType,
			boolean multiline, String alignStr) {
		addElementRegion(text, element,
				getAttributeContentType(attributeType, element.isImplicit()),
				manipulation, multiline, alignStr);
	}

	protected void addLabelRegion(String text, ILElement element) {
		addElementRegion(text, element, LABEL_TYPE, null, false, text);
	}

	protected void addLeftPresentationRegion(String text, ILElement element) {
		addElementRegion(text, element, LEFT_PRESENTATION_TYPE, null, false,
				text);
	}
	
	protected void addPresentationRegion(String text, ILElement element) {
		addElementRegion(text, element, PRESENTATION_TYPE, null, false, text);
	}

	protected void addCommentHeaderRegion(ILElement element) {
		addElementRegion(COMMENT_HEADER_DELIMITER, element,
				COMMENT_HEADER_TYPE, null, false, null);
	}

	protected void addKeywordRegion(String title, ILElement element) {
		appendPresentationTabs(element);
		addElementRegion(title, element, KEYWORD_TYPE, null, false, title);
		appendLineSeparator(element);
	}

	protected void addSectionRegion(String title, ILElement element) {
		appendPresentationTabs(element);
		addElementRegion(title, element, KEYWORD_TYPE, null, false, title);
		appendLineSeparator(element);
	}

	public static String getTabs(int number) {
		final StringBuilder tabs = new StringBuilder();
		for (int i = 0; i < number; i++) {
			tabs.append(TAB);
		}
		return tabs.toString();
	}
	
	public List<EditorRegion> getRegions() {
		return regions;
	}

	public void incrementIndentation() {
		level++;
	}
	
	public void decrementIndentation() {
		level--;
	}
	
	public void appendLineSeparator(ILElement element) {
		addPresentationRegion((String) LINESEPARATOR, element);
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getLength() {
		return builder.length();
	}
	
	public String getText() {
		return builder.toString();
	}

	public void appendLeftPresentationTabs(ILElement e) {
		addLeftPresentationRegion(getTabs(level), e);
	}

	public void appendPresentationTabs(ILElement e, int indentation) {
		addPresentationRegion(getTabs(indentation), e);
	}
	
	public void appendPresentationTabs(ILElement e) {
		addPresentationRegion(getTabs(level), e);
	}
	
	public void appendElementHandle(ILElement element, ContentType contentType) {
		// \u26ac is the handle character "⚬"
		final String s = String.format("%s", "\u26ac\t");
		addElementRegion(s, element, contentType, null, false, s);
	}

	public void incrementIndentation(int i) {
		level += i;
	}
	
	public void decrementIndentation(int i) {
		level -= i;
	}

	public void appendAlignementTab(ILElement e) {
		addPresentationRegion(TAB, e);
	}
	
}
