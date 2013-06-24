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
public class EditPos implements Cloneable {

	public static final EditPos INVALID_POS = new EditPos(Integer.MAX_VALUE, 0);

	public static int computeEnd(int offset, int length) {
		return offset + length - 1;
	}
	
	public static int computeLength(int start, int end) {
		return end - start + 1;
	}
	
	public static EditPos newPosOffLen(int offset, int length) {
		return newPosOffLen(offset, length, true);
	}

	public static EditPos newPosOffLen(int offset, int length, boolean checked) {
		if (!isValidOffLen(offset, length, true)) {
			if (checked) {
				throw new IllegalArgumentException("invalid offset/length : "
						+ offset + ", " + length);
			} else {
				return INVALID_POS;
			}
		}
		return new EditPos(offset, length);
	}

	public static EditPos newPosStartEnd(int start, int end) {
		return newPosStartEnd(start, end, true);
	}

	public static EditPos newPosStartEnd(int start, int end, boolean checked) {
		if (!isValidStartEnd(start, end, true)) {
			if (checked) {
				throw new IllegalArgumentException("invalid start/end : "
						+ start + ", " + end);
			} else {
				return INVALID_POS;
			}
		}
		final int length = computeLength(start, end);
		return new EditPos(start, length);
	}

	public static boolean isValidOffLen(int offset, int length,
			boolean emptyAllowed) {
		return offset >= 0 && emptyAllowed ? length >= 0 : length > 0;
	}

	public static boolean isValidStartEnd(int start, int end,
			boolean emptyAllowed) {
		if (start < 0 || end < 0)
			return false;
		final int length = computeLength(start, end);
		return isValidOffLen(start, length, emptyAllowed);
	}

	private final int offset;
	private final int length;

	private EditPos(int offset, int length) {
		Assert.isLegal(isValidOffLen(offset, length, true));
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
		return computeEnd(offset,length);
	}

	public Point toPoint() {
		return new Point(getStart(), getEnd());
	}

	public Position toPosition() {
		return new Position(getOffset(), getLength());
	}

	public boolean includes(int index) {
		return getStart() <= index && index < getStart() + getLength();
	}
	
	public boolean includes(EditPos other) {
		return this.getStart() <= other.getStart()
				&& other.getEnd() <= this.getEnd();
	}
	
	
	public boolean isIncludedOrTouches(int index) {
		return getStart() <= index && index <= getStart() + getLength();
	}
	
	public boolean overlapsWith(EditPos other) {
		return this.getStart() <= other.getEnd()
				&& other.getStart() <= this.getEnd();
	}

	@Override
	public EditPos clone() {
		return new EditPos(offset, length);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + length;
		result = prime * result + offset;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof EditPos)) {
			return false;
		}
		EditPos other = (EditPos) obj;
		if (length != other.length) {
			return false;
		}
		if (offset != other.offset) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		if (this == INVALID_POS) {
			return "INVALID POSITION";
		}
		final StringBuilder sb = new StringBuilder();
		sb.append(getStart());
		sb.append(", ");
		sb.append(getEnd());
		sb.append(", (");
		sb.append(getLength());
		sb.append(")");
		return sb.toString();
	}
}
