/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.Position;
import org.eclipse.swt.graphics.Point;

/**
 * Editor position. Combines usage of Position (offset, length) and Point
 * (start, end).
 * 
 * @author Nicolas Beauger
 * 
 */
public class EditPos {

	public static EditPos newPosOffLen(int offset, int length) {
		return new EditPos(offset, length);
	}
	
	public static EditPos newPosStartEnd(int start, int end) {
		final int length = end - start + 1;
		return new EditPos(start, length);
	}
	
	private final int offset;
	private final int length;

	private EditPos(int offset, int length) {
		Assert.isLegal(offset>=0);
		Assert.isLegal(length >= 0);
		this.offset = offset;
		this.length = length;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}

	public int getStart() {
		return offset;
	}

	public int getEnd() {
		return offset + length - 1;
	}

	public Point toPoint() {
		return new Point(getStart(), getEnd());
	}

	public Position toPosition() {
		return new Position(getOffset(), getLength());
	}

	public boolean includes(int index) {
		return getStart() <= index && index <= getEnd();
	}
}
