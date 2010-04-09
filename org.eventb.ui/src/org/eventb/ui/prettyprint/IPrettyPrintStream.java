/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.prettyprint;

/**
 * Interface describing the pretty print stream used to render the components in
 * the pretty print view.
 * 
 * @author Thomas Muller
 * @since 1.2
 */
public interface IPrettyPrintStream {

	/**
	 * Appends an empty line to the given stream.
	 */
	public void appendEmptyLine();

	/**
	 * Appends a keyword to the current stream according to the stream's current
	 * level of indentation.
	 * 
	 * @param keyword
	 *            the string corresponding to the keyword
	 */
	public void appendKeyword(String keyword);

	/**
	 * Method that appends to the given string builder the beginning of an
	 * incremented indentation level.
	 */
	public void appendLevelBegin();

	/**
	 * Method that appends to the given string builder the end of indentation at
	 * its current level and decrements the indentation level.
	 */
	public void appendLevelEnd();

	/**
	 * Method to be used by contributors to pretty print an element.
	 * 
	 * @param s
	 *            the element corresponding string to be appended (for example,
	 *            an identifier, a label, a predicate string...)
	 * @param begin
	 *            the string that should be appended before the element string,
	 *            which is typically retrieved by <code>getElementBegin()</code>
	 *            and is used to create the html structure
	 * @param end
	 *            the string that should be appended after the element string,
	 *            which is typically retrieved by <code>getElementEnd()</code>
	 *            and is used to create the html structure
	 * @param beginSeparator
	 *            the separator beginning to be appended
	 * @param endSeparator
	 *            the separator ending to be appended
	 */
	public void appendString(String s, String begin, String end,
			String beginSeparator, String endSeparator);

	/**
	 * Decrements the current level of indentation
	 */
	public void decrementLevel();

	/**
	 * Increments the current level of indentation
	 */
	public void incrementLevel();
}