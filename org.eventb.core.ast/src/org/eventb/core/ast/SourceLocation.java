/*
 * Created on Mar 13, 2005
 */
package org.eventb.core.ast;


/**
 * Location of a formula in a string.
 * <p>
 * This class models the source location of a formula in a string, that is a
 * pair of integers (<code>start</code>, <code>end</code>) that
 * represents the index of the first and last characters of this formula in the
 * originating string.
 * <p>
 * The objects of this class are immutable.
 * 
 * @author Laurent Voisin
 */
public class SourceLocation {

	// The start and end position in the string 
	// from which this formula was parsed
	private final int start;
	private final int end;

	/**
	 * Creates a new source location.
	 * @param start
	 * @param end
	 */
	public SourceLocation(final int start, final int end) {
		this.start = start;
		this.end = end;
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SourceLocation) {
			SourceLocation temp = (SourceLocation) obj;
			return temp.end == end && temp.start == start;
		}
		return false;
	}

	@Override
	public String toString() {
		return start + ":" + end;
	}
}
