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


/**
 * Utility class for text processing.
 */
public class RodinTextGeneratorUtils {

	public static int MIN_LEVEL = 1;
	public static final String COMMENT_HEADER_DELIMITER = "§";
	public static final Character TAB = '\u0009';
	public static final int NONE = 0;
	public static final String WHITESPACE = " ";
	public static final Object LINESEPARATOR = System
			.getProperty("line.separator");
	
	public static String processMulti(boolean multiLine, int level,
			boolean addWhiteSpace, String text) {
		if (!multiLine || text == null)
			return text;
		final String regex = "(\r\n)|(\r)|(\n)";
		if (addWhiteSpace)
			return text.replaceAll(regex, "$0" + getTabs(level) + WHITESPACE);
		return text.replaceAll(regex, "$0" + getTabs(level));
	}

	public static String deprocessMulti(int level, boolean multiLine,
			boolean tabbed, String text) {
		if (!multiLine)
			return text;
		return deprocessMulti(level, tabbed, text);
	}

	public static String deprocessMulti(int level, boolean addWhitespace,
			String text) {
		final String commonPatternStart = "((\r\n)|(\r)|(\n))(";
		// Tells that it should take into account one (only) matching pattern
		final String commonPatternEnd = "){1}";
		if (addWhitespace) {
			return text.replaceAll(commonPatternStart + getTabs(level)
					+ WHITESPACE + commonPatternEnd, "$1");
		}
		return text.replaceAll(commonPatternStart + getTabs(level)
				+ commonPatternEnd, "$1");
	}
	
	private static String getTabs(int number) {
		StringBuilder tabs = new StringBuilder();
		for (int i = 0; i < number; i++) {
			tabs.append(TAB);
		}
		return tabs.toString();
	}
	
}
