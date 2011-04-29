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

import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.presentation.RodinConfiguration.ContentType;

/**
 * Information produced by the text generator necessary to build the document
 * and create the document mapping.
 */
public class RegionInfo {

	final String text;
	final ILElement element;
	final ContentType type;
	final IAttributeManipulation manipulation;
	final int start;
	final int length;
	final int indentation;
	final int additionalTabs;
	final boolean multiline;
	final boolean addWhitespace;

	public RegionInfo(String text, ILElement element, ContentType type,
			IAttributeManipulation manipulation, int start, int length,
			int indentation, int additionalTabs, boolean multiline,
			boolean addWhitespace) {
		this.text = text;
		this.element = element;
		this.type = type;
		this.manipulation = manipulation;
		this.start = start;
		this.length = length;
		this.indentation = indentation;
		this.additionalTabs = additionalTabs;
		this.multiline = multiline;
		this.addWhitespace = addWhitespace;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return the element
	 */
	public ILElement getElement() {
		return element;
	}

	/**
	 * @return the type
	 */
	public ContentType getType() {
		return type;
	}

	/**
	 * @return the manipulation
	 */
	public IAttributeManipulation getManipulation() {
		return manipulation;
	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @return the indentation
	 */
	public int getIndentation() {
		return indentation;
	}

	/**
	 * @return the additionalTabs
	 */
	public int getAdditionalTabs() {
		return additionalTabs;
	}

	/**
	 * @return the multiline
	 */
	public boolean isMultiline() {
		return multiline;
	}

	/**
	 * @return the addWhitespace
	 */
	public boolean isAddWhitespace() {
		return addWhitespace;
	}
	
}
