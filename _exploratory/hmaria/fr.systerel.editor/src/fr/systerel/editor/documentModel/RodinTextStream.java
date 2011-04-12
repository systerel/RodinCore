/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.documentModel;

import static fr.systerel.editor.presentation.RodinConfiguration.COMMENT_HEADER_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.KEYWORD_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.LABEL_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.PRESENTATION_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.SECTION_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.getAttributeContentType;

import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.presentation.RodinConfiguration.ContentType;

/**
 * A class holding a string builder and the documentMapper used by the text
 * generator in order to process both the text representation of a model, and
 * the interval mapping of the model. 
 */
public class RodinTextStream {

	private static final String COMMENT_HEADER_DELIMITER = "§";
	private static final Character TAB = '\u0009';
	private static final int NO_TABS = 0;
	private static final String WHITESPACE = " ";
	private static final Object LINESEPARATOR = System
			.getProperty("line.separator");
	
	private int level = 0;
	private final StringBuilder builder;
	private final DocumentMapper mapper;

	public static String processMulti(boolean multiLine, int level,
			boolean addWhiteSpace, String text) {
		if (!multiLine || text == null)
			return text;
		final String regex = "(\r\n)|(\r)|(\n)";
		if (addWhiteSpace)
			return text.replaceAll(regex, "$0" + getTabs(level) + WHITESPACE);
		return text.replaceAll(regex, "$0" + getTabs(level));
	}

	public static String deprocessMulti(int level, boolean multiLine,
			boolean tabbed, String text) {
		if (!multiLine)
			return text;
		return deprocessMulti(level, tabbed, text);
	}

	public static String deprocessMulti(int level, boolean tabbed, String text) {
		final String commonPatternStart = "((\r\n)|(\r)|(\n))(";
		// Tells that it should take into account one (only) matching pattern 
		final String commonPatternEnd = "){1}";
		if (!tabbed) {
			return text.replaceAll(commonPatternStart + getTabs(level)
					+ WHITESPACE + commonPatternEnd, "$1");
		}
		return text.replaceAll(commonPatternStart + getTabs(level)
				+ commonPatternEnd, "$1");
	}

	public RodinTextStream(DocumentMapper mapper) {
		this.builder = new StringBuilder();
		this.mapper = mapper;
		mapper.resetPrevious();
	}

	protected void addEditorSection(IInternalElementType<?> type,
			int folding_start, int folding_length) {
		mapper.addEditorSection(type, folding_start, folding_length);
	}

	protected void addElementRegion(String text, ILElement element,
			ContentType contentType, boolean multiLine) {
		addElementRegion(text, element, contentType, null, multiLine, NO_TABS);
	}

	protected void addElementRegion(String text, ILElement element,
			ContentType contentType, boolean multiLine, int additionalTabs) {
		addElementRegion(text, element, contentType, null, multiLine,
				additionalTabs);
	}

	protected void addElementRegion(String text, ILElement element,
			ContentType contentType, IAttributeManipulation manipulation,
			boolean multiLine, int additionalTabs) {
		final int start = builder.length();
		final String multilined = processMulti(multiLine, getLevel()
				+ additionalTabs, additionalTabs == 0, text);
		builder.append(multilined);
		final int length = builder.length() - start;
		mapper.processInterval(start, length, element, contentType,
				manipulation, multiLine, getLevel() + additionalTabs,
				additionalTabs != 0);
	}

	protected void addAttributeRegion(String text, ILElement element,
			IAttributeManipulation manipulation, IAttributeType attributeType) {
		addElementRegion(text, element, getAttributeContentType(attributeType),
				manipulation, false, NO_TABS);
	}

	protected void addLabelRegion(String text, ILElement element) {
		addElementRegion(text, element, LABEL_TYPE, false);
		builder.append(LINESEPARATOR);
	}

	protected void addPresentationRegion(String text, ILElement element) {
		addElementRegion(text, element, PRESENTATION_TYPE, false);
	}

	protected void addCommentHeaderRegion(ILElement element, boolean appendTabs) {
		if (appendTabs)
			builder.append(getTabs(level));
		addElementRegion(COMMENT_HEADER_DELIMITER, element,
				COMMENT_HEADER_TYPE, false);
	}

	protected void addKeywordRegion(String title) {
		addPresentationRegion(getTabs(level), null);
		addElementRegion(title, null, KEYWORD_TYPE, false);
		builder.append(LINESEPARATOR);
	}

	protected void addSectionRegion(String title) {
		if (level > 0)
			addPresentationRegion(getTabs(level), null);
		addElementRegion(title, null, SECTION_TYPE, false);
		builder.append((String) LINESEPARATOR);
	}

	public static String getTabs(int number) {
		StringBuilder tabs = new StringBuilder();
		for (int i = 0; i < number; i++) {
			tabs.append(TAB);
		}
		return tabs.toString();
	}

	public void incrementIndentation() {
		level++;
	}
	
	public void decrementIndentation() {
		level--;
	}
	
	public void appendLineSeparator() {
		builder.append(LINESEPARATOR);
	}
	
	public void appendPresentationTabs(ILElement e) {
		addPresentationRegion(getTabs(level), e);
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getLength() {
		return builder.length();
	}
	
	public String getTabsForCurrentLevel() {
		return getTabs(level);
	}
	
	public String getText() {
		return builder.toString();
	}
	
}
