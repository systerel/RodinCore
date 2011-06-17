/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package fr.systerel.editor.internal.documentModel;

import static fr.systerel.editor.internal.editors.EditPos.newPosOffLen;

import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.editors.EditPos;
import fr.systerel.editor.internal.presentation.RodinConfiguration.ContentType;

/**
 * An interval represents a range in a document associated with a
 * <code>LightElement</code> and a content type.<br>
 * The <code>LightElement</code> is not mandatory.
 */
public class Interval implements Comparable<Interval> {

	private EditPos pos;

	private final ILElement element;
	private final IRodinElement rodinElement;
	private final ContentType contentType;
	private final IAttributeManipulation attManip;
	private final boolean multiLine;
	private final boolean addWhiteSpace;
	private boolean changed;
	private int indentation;
	
	public Interval(EditPos pos, ILElement element, IInternalElementType<?> elementType,
			ContentType contentType, IAttributeManipulation attManip,
			boolean multiLine, boolean addWhiteSpace) {
		this.pos = pos;
		this.element = element;
		this.attManip = attManip;
		this.rodinElement = getElement(element);
		this.contentType = contentType;
		this.multiLine = multiLine;
		this.addWhiteSpace = addWhiteSpace;
	}

	private IRodinElement getElement(ILElement element) {
		if (element != null)
			return (IRodinElement) element.getElement();
		return null;
	}

	public int compareTo(Interval o) {
		return pos.getOffset() - o.pos.getOffset();
	}

	public int getOffset() {
		return pos.getOffset();
	}

	public void setOffset(int offset) {
		this.pos = newPosOffLen(offset, pos.getLength());
	}

	public int getLength() {
		return pos.getLength();
	}

	public void setLength(int length) {
		this.pos = newPosOffLen(pos.getOffset(), length);
	}

	public void setPos(EditPos pos) {
		this.pos = pos;
	}
	
	public int getLastIndex() {
		return pos.getEnd();
	}
	
	public IRodinElement getRodinElement() {
		return rodinElement;
	}

	public ILElement getElement() {
		return element;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public IAttributeManipulation getAttributeManipulation() {
		return attManip;
	}

	public String[] getPossibleValues() {
		if (attManip == null)
			return null;
		return attManip.getPossibleValues(rodinElement, null);
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	/**
	 * Tells if this interval is editable.
	 */
	public boolean isEditable() {
		return contentType.isEditable();
	}
	
	public boolean isMultiLine() {
		return multiLine;
	}
	
	public boolean isAddWhiteSpace() {
		return addWhiteSpace;
	}
	
	public void setIndentation(int indentation) {
		this.indentation = indentation;
	}
	
	public int getIndentation() {
		return indentation;
	}

	public boolean contains(int offset) {
		return pos.includes(offset); 
	}
}
