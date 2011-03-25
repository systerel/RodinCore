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

package fr.systerel.editor.documentModel;

import java.util.Arrays;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.lightcore.LightElement;

import fr.systerel.editor.editors.RodinConfiguration;

/**
 * An interval represents a range in a document associated with a
 * <code>LightElement</code> and a content type.<br>
 * The <code>LightElement</code> is not mandatory.
 */
public class Interval implements Comparable<Interval> {

	private int offset;
	private int length;

	private final LightElement element;
	private final IRodinElement rodinElement;
	private String contentType;
	private boolean changed;

	private static String[] editableTypes = { //
	RodinConfiguration.COMMENT_TYPE, //
			RodinConfiguration.CONTENT_TYPE, //
			RodinConfiguration.IDENTIFIER_TYPE, //
	};

	public Interval(int offset, int length, LightElement element,
			String contentType) {
		this.offset = offset;
		this.length = length;
		this.element = element;
		this.rodinElement = getRodinElement(element);
		this.contentType = contentType;
	}

	private IRodinElement getRodinElement(LightElement element) {
		if (element != null)
			return (IRodinElement) element.getERodinElement();
		return null;
	}

	public int compareTo(Interval o) {
		return offset - o.offset;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public IRodinElement getRodinElement() {
		return rodinElement;
	}

	public LightElement getElement() {
		return element;
	}

	public String getContentType() {
		return contentType;
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
		return Arrays.asList(editableTypes).contains(contentType);
	}

	public static boolean isEditableType(String contentType) {
		return Arrays.asList(editableTypes).contains(contentType);
	}

}
