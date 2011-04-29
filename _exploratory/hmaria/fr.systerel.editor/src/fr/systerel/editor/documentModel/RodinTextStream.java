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

import static fr.systerel.editor.documentModel.RodinTextGeneratorUtils.COMMENT_HEADER_DELIMITER;
import static fr.systerel.editor.documentModel.RodinTextGeneratorUtils.LINESEPARATOR;
import static fr.systerel.editor.documentModel.RodinTextGeneratorUtils.MIN_LEVEL;
import static fr.systerel.editor.documentModel.RodinTextGeneratorUtils.NONE;
import static fr.systerel.editor.documentModel.RodinTextGeneratorUtils.TAB;
import static fr.systerel.editor.documentModel.RodinTextGeneratorUtils.processMulti;
import static fr.systerel.editor.presentation.RodinConfiguration.COMMENT_HEADER_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.KEYWORD_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.LABEL_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.LEFT_PRESENTATION_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.PRESENTATION_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.getAttributeContentType;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.presentation.RodinConfiguration;
import fr.systerel.editor.presentation.RodinConfiguration.ContentType;

/**
 * A class holding a string builder and the documentMapper used by the text
 * generator in order to process both the text representation of a model, and
 * the interval mapping of the model.
 */
public class RodinTextStream {

	private final StringBuilder builder;
	private final List<RegionInfo> intervalInfos;
	private int level = MIN_LEVEL;

	public RodinTextStream() {
		this.builder = new StringBuilder();
		this.intervalInfos = new ArrayList<RegionInfo>();
	}

	public void append(RegionInfo info) {
		builder.append(info.getText());
	}

	public RegionInfo getElementRegion(int start, int level, String text,
			ILElement element, ContentType contentType,
			IAttributeManipulation manipulation, boolean multiline,
			int additionalTabs) {
		final boolean addWhitespace = contentType == RodinConfiguration.COMMENT_TYPE
				|| contentType == RodinConfiguration.IMPLICIT_COMMENT_TYPE;
		final String multilined = processMulti(multiline, getLevel()
				+ additionalTabs, addWhitespace, text);
		final int length = multilined.length();
		return new RegionInfo(text, element, contentType, manipulation, start,
				length, level, additionalTabs, multiline, addWhitespace);
		// mapper.processInterval(start, length, element, contentType,
		// manipulation, multiLine, getLevel() + additionalTabs,
		// addWhiteSpace);
	}

	public void appendRegion(String text, ILElement element,
			ContentType contentType, IAttributeManipulation manipulation,
			boolean multiline, int additionalTabs) {
		final int start = builder.length();
		final RegionInfo info = getElementRegion(start, getLevel(), text,
				element, contentType, manipulation, multiline, additionalTabs);
		intervalInfos.add(info);
		append(info);
	}

	public void appendAttributeRegion(String text, ILElement element,
			IAttributeManipulation manipulation, IAttributeType attributeType) {
		appendRegion(text, element, getAttributeContentType(attributeType),
				manipulation, false, NONE);
	}
	
	public RegionInfo getAttributeRegion(int start, int level, String text,
			ILElement element, ContentType contentType,
			IAttributeManipulation manipulation, IAttributeType attributeType,
			boolean multiline, int additionalTabs) {
		return getElementRegion(start, level, text, element,
				getAttributeContentType(attributeType), manipulation,
				multiline, additionalTabs);
	}
	

	public void appendLabelRegion(String text, ILElement element) {
		appendRegion(text, element, LABEL_TYPE, null, false, NONE);
		appendPresentationRegion((String) LINESEPARATOR, element);
	}

	public void appendLeftPresentationRegion(String text, ILElement element) {
		appendRegion(text, element, LEFT_PRESENTATION_TYPE, null, false,
				NONE);
	}

	public void appendPresentationRegion(String text, ILElement element) {
		appendRegion(text, element, PRESENTATION_TYPE, null, false, NONE);
	}

	public void appendCommentHeaderRegion(ILElement element, boolean appendTabs) {
		if (appendTabs)
			appendPresentationRegion(String.valueOf(TAB), element);
		appendRegion(COMMENT_HEADER_DELIMITER, element,
				COMMENT_HEADER_TYPE, null, false, NONE);
	}

	public void addKeywordRegion(String title) {
		appendPresentationTabs(null);
		appendRegion(title, null, KEYWORD_TYPE, null, false, NONE);
		appendLineSeparator();
	}

	public void appendClauseRegion(String title) {
		if (level > 0)
			appendPresentationTabs(null);
		appendRegion(title, null, KEYWORD_TYPE, null, false, NONE);
		appendLineSeparator();
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
		appendPresentationRegion((String) LINESEPARATOR, null);
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

	public void appendLeftPresentationTabs(ILElement e) {
		appendLeftPresentationRegion(getTabs(level), e);
	}

	public void appendPresentationTabs(ILElement e, int indentation) {
		appendPresentationRegion(getTabs(indentation), e);
	}

	public void appendPresentationTabs(ILElement e) {
		appendPresentationRegion(getTabs(level), null);
	}

	public void incrementIndentation(int i) {
		level += i;
	}

	public void decrementIndentation(int i) {
		level -= i;
	}

	/**
	 * @return the intervalInfos to read in order to process the document's
	 *         intervals.
	 */
	public List<RegionInfo> getIntervalInfos() {
		return intervalInfos;
	}

}
