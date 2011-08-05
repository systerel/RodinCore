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
package org.eventb.internal.ui.eventbeditor.htmlpage;

import static org.eventb.internal.ui.eventbeditor.htmlpage.AstConverter.BEGIN_COMMENT;
import static org.eventb.internal.ui.eventbeditor.htmlpage.AstConverter.BEGIN_COMMENT_SEPARATOR;
import static org.eventb.internal.ui.eventbeditor.htmlpage.AstConverter.END_COMMENT;
import static org.eventb.internal.ui.eventbeditor.htmlpage.AstConverter.END_COMMENT_SEPARATOR;

import java.util.StringTokenizer;

import org.eventb.core.ICommentedElement;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintUtils;
import org.rodinp.core.RodinDBException;

/**
 * Class used to hold the pretty print stream.
 * 
 * @since 1.2
 */
public class PrettyPrintStream implements IPrettyPrintStream {

	private static final int VOID_INDENTATION = 0;

	private final StringBuilder stringBuilder;

	private int currentLevel = VOID_INDENTATION;

	public PrettyPrintStream() {
		this.stringBuilder = new StringBuilder();
	}

	/**
	 * Returns the string builder associated with this stream.
	 */
	public StringBuilder getStringBuilder() {
		return stringBuilder;
	}

	/**
	 * Returns the current level of indentation
	 */
	public int getCurrentLevel() {
		return currentLevel;
	}

	/**
	 * Sets the current level of indentation
	 */
	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}
	
	@Override
	public void incrementLevel(){
		this.currentLevel = currentLevel + 1;
	}
	
	@Override
	public void decrementLevel(){
		this.currentLevel = currentLevel -1 ;
	}

	/*
	 * Should not be used outside here
	 */
	private void appendComment(String comment) {
		appendString(comment, //
				BEGIN_COMMENT, //
				END_COMMENT, //
				BEGIN_COMMENT_SEPARATOR, //
				END_COMMENT_SEPARATOR);
	}

	/**
	 * Append the comment attached to this element, if any.
	 * @param element
	 *            the commented element
	 */
	public void appendComment(ICommentedElement element) {
		try {
			if (element.hasComment()) {
				String comment = element.getComment();
				if (comment.length() != 0)
					appendComment(PrettyPrintUtils
							.wrapString(comment));
			}
		} catch (RodinDBException e) {
			// ignore
			if (UIUtils.DEBUG)
				e.printStackTrace();
		}
	}

	/**
	 * Method to be used by contributors to pretty print an element.
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
	@Override
	public void appendString(String s, String begin,
			String end, String beginSeparator, String endSeparator) {
		StringTokenizer stringTokenizer = new StringTokenizer(s, "\n");
		
		if (stringTokenizer.countTokens() <= 1) {
			stringBuilder.append(begin);
			if (beginSeparator != null) {
				stringBuilder.append(AstConverter.SPACE);
				stringBuilder.append(beginSeparator);
				stringBuilder.append(AstConverter.SPACE);
			}
			stringBuilder.append(s);
			if (endSeparator != null) {
				stringBuilder.append(AstConverter.SPACE);
				stringBuilder.append(endSeparator);
				stringBuilder.append(AstConverter.SPACE);
			}
			stringBuilder.append(end);
		} else {
			// Printing multi-line
			stringBuilder.append(AstConverter.BEGIN_MULTILINE);
			while (stringTokenizer.hasMoreTokens()) {
				String text = stringTokenizer.nextToken();
				stringBuilder.append(AstConverter.BEGIN_LINE);
				stringBuilder.append(begin);
				if (beginSeparator != null) {
					stringBuilder.append(AstConverter.SPACE);
					stringBuilder.append(beginSeparator);
					stringBuilder.append(AstConverter.SPACE);
				}
				stringBuilder.append(text);
				if (endSeparator != null) {
					stringBuilder.append(AstConverter.SPACE);
					stringBuilder.append(endSeparator);
					stringBuilder.append(AstConverter.SPACE);
				}
				stringBuilder.append(end);
				stringBuilder.append(AstConverter.END_LINE);
			}
			stringBuilder.append(AstConverter.END_MULTILINE);
		}
	}

	/**
	 * Method that appends to the given string builder the end of indentation of
	 * a given level.
	 */
	@Override
	public void appendLevelEnd() {
		switch (currentLevel) {
		case 0:
			stringBuilder.append(AstConverter.END_LEVEL_0);
			break;
		case 1:
			stringBuilder.append(AstConverter.END_LEVEL_1);
			break;
		case 2:
			stringBuilder.append(AstConverter.END_LEVEL_2);
			break;
		case 3:
			stringBuilder.append(AstConverter.END_LEVEL_3);
		default:
			stringBuilder.append(AstConverter.END_LEVEL_0);
			break;
		}
		decrementLevel();
	}

	/**
	 * Method that appends to the given string builder the beginning of
	 * indentation at a given level.
	 */
	@Override
	public void appendLevelBegin() {
		incrementLevel();
		switch (currentLevel) {
		case 0:
			stringBuilder.append(AstConverter.BEGIN_LEVEL_0);
			break;
		case 1:
			stringBuilder.append(AstConverter.BEGIN_LEVEL_1);
			break;
		case 2:
			stringBuilder.append(AstConverter.BEGIN_LEVEL_2);
			break;
		case 3:
			stringBuilder.append(AstConverter.BEGIN_LEVEL_3);
			break;
		default:
			stringBuilder.append(AstConverter.BEGIN_LEVEL_0);
			break;
		}
	}

	@Override
	public void appendKeyword(String str) {
		switch (currentLevel) {
		case 0:
			stringBuilder.append(AstConverter.BEGIN_MASTER_KEYWORD);
			break;
		default:
			stringBuilder.append(AstConverter.BEGIN_KEYWORD_1);
			break;
		}
	
		stringBuilder.append(str);
	
		switch (currentLevel) {
		case 0:
			stringBuilder.append(AstConverter.END_MASTER_KEYWORD);
			break;
		default:
			stringBuilder.append(AstConverter.END_KEYWORD_1);
			break;
		}
	}

	/**
	 * Appends an empty line to the given stream hold by the given string
	 * builder.
	 */
	@Override
	public void appendEmptyLine() {
		stringBuilder.append(AstConverter.EMPTY_LINE);
	}
	
	
}
