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

	private static final Character tab = '\u0009';
	static final Object lineSeparator = System
			.getProperty("line.separator");
	
	private int level = 0;
	private final StringBuilder builder;
	private final DocumentMapper mapper;
	
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
		addElementRegion(text, element,	contentType, null, multiLine);
	}
	
	protected void addElementRegion(String text, ILElement element,
			ContentType contentType,IAttributeManipulation manipulation, boolean multiLine) {
		final int start = builder.length();
		builder.append(text);
		final int length = builder.length() - start;
		mapper.processInterval(start, length, element, contentType, manipulation, multiLine);
	}

	protected void addAttributeRegion(String text, ILElement element,
			IAttributeManipulation manipulation, IAttributeType attributeType) {
		addElementRegion(text, element, getAttributeContentType(attributeType), manipulation, false);
	}

	protected void addLabelRegion(String text, ILElement element) {
		addElementRegion(text, element, LABEL_TYPE, false);
		builder.append(lineSeparator);
	}

	protected void addPresentationRegion(String text, ILElement element) {
		addElementRegion(text, element, PRESENTATION_TYPE, false);
	}

	protected void addCommentHeaderRegion(ILElement element, boolean appendTabs) {
		if (appendTabs)
			builder.append(getTabs(level));
		addElementRegion("§", element, COMMENT_HEADER_TYPE, false);
	}

	protected void addKeywordRegion(String title) {
		addPresentationRegion(getTabs(level), null);
		addElementRegion(title, null, KEYWORD_TYPE, false);
		builder.append(lineSeparator);
	}

	protected void addSectionRegion(String title) {
		if (level > 0)
			addPresentationRegion(getTabs(level), null);
		addElementRegion(title, null, SECTION_TYPE, false);
		builder.append((String) lineSeparator);
	}

	private static String getTabs(int number) {
		StringBuilder tabs = new StringBuilder();
		for (int i = 0; i < number; i++) {
			tabs.append(tab);
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
		builder.append(lineSeparator);
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
