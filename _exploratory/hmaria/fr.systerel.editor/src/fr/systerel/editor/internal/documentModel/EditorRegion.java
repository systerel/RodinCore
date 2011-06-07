/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.documentModel;

import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.presentation.RodinConfiguration;
import fr.systerel.editor.internal.presentation.RodinConfiguration.ContentType;

/**
 * Material needed to build an interval in the Rodin Editor document.
 */
public class EditorRegion {

	private final boolean addWhitespace;
	private final int startOffset;
	private final int length;
	private final int level;
	private final String elementText;
	private final ILElement element;
	private final ContentType type;
	private final IAttributeManipulation manipulation;
	private final Boolean multiline;
	private final int additionalTabs;

	public EditorRegion(int startOffset, int level, String text,
			ILElement element, ContentType type,
			IAttributeManipulation manipulation, boolean multiline,
			int additionalTabs) {
		this.addWhitespace = (type == RodinConfiguration.COMMENT_TYPE || type == RodinConfiguration.IMPLICIT_COMMENT_TYPE);
		this.startOffset = startOffset;
		this.level = level;
		this.elementText = RodinTextStream.processMulti(multiline,
				additionalTabs, addWhitespace, text);
		this.length = elementText.length();
		this.element = element;
		this.type = type;
		this.manipulation = manipulation;
		this.multiline = multiline;
		this.additionalTabs = additionalTabs;
	}
	
	public int getStartOffset() {
		return startOffset;
	}

	public int getLength() {
		return length;
	}

	public String getText() {
		return elementText;
	}

	public ILElement getElement() {
		return element;
	}

	public ContentType getType() {
		return type;
	}

	public IAttributeManipulation getManipulation() {
		return manipulation;
	}

	public Boolean getMultiline() {
		return multiline;
	}

	public int getAdditionalTabs() {
		return additionalTabs;
	}
	
	public boolean isAddWhitespace() {
		return addWhitespace;
	}
	
}
