/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added origin
 *******************************************************************************/
package org.eventb.core.ast;


/**
 * Location of a formula in a string.
 * <p>
 * This class models the source location of a formula in a string, that is a
 * pair of integers (<code>start</code>, <code>end</code>) that
 * represents the index of the first and last characters of this formula in the
 * originating string (indexes are counted from 0).
 * <p>
 * The objects of this class are immutable.
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public class SourceLocation {

	// The start and end position in the string 
	// from which this formula was parsed
	private final int start;
	private final int end;
	private final Object origin;

	/**
	 * Creates a new source location.
	 * @param start
	 * @param end
	 */
	public SourceLocation(final int start, final int end) {
		this(start, end, null);
	}

	/**
	 * Creates a new source location.
	 * @param start
	 * @param end
	 * @param origin
	 */
	public SourceLocation(final int start, final int end, final Object origin) {
		this.start = start;
		this.end = end;
		this.origin = origin;
	}

	/**
	 * Tells whether this source location contains the given source location.
	 * 
	 * @param other
	 *            another source location
	 * @return <code>true</code> iff this source contains the given location.
	 */
	public final boolean contains(SourceLocation other) {
		return this.start <= other.start && other.end <= this.end;
	}
	
	/**
	 * Returns the start index.
	 * @return Returns the start index.
	 */
	public final int getStart() {
		return start;
	}

	/**
	 * Returns the end index.
	 * @return Returns the end index.
	 */
	public final int getEnd() {
		return end;
	}
	
	/**
	 * Returns the origin.
	 * @return the origin
	 */
	public Object getOrigin() {
		return origin;
	}

	@Override
	public int hashCode() {
		return (1 << 16) * start + end;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof SourceLocation) {
			final SourceLocation other = (SourceLocation) obj;
			return this.start == other.start && this.end == other.end;
		}
		return false;
	}

	@Override
	public String toString() {
		return start + ":" + end;
	}
}
