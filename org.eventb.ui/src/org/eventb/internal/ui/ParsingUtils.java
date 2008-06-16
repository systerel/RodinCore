/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui;

class ParsingUtils {

	/**
	 * constants storing the string length of biggest int and long values
	 */
	private static final int maxIntLength = Integer.toString(Integer.MAX_VALUE)
			.length();
	private static final int maxLongLength = Long.toString(Long.MAX_VALUE)
			.length();

	/**
	 * enumerated type representing the parseability of a given String
	 */
	public static enum Parseability {
		PARSEABLE_INT, PARSEABLE_LONG, NOT_PARSEABLE
	}

	/**
	 * Determine the parseability of a given String
	 * 
	 * @param toBeParsed :
	 *            the string to be parsed
	 * @return an element of type Parseability
	 */
	public static Parseability determineParseability(String toBeParsed) {
		int toBeParsedLength = toBeParsed.length();
		boolean parseableToInt = false;
		boolean parseableToLong = false;

		if (toBeParsedLength >= maxIntLength) { // >= => try long
			if (toBeParsedLength > maxLongLength) {
				parseableToInt = false;
				parseableToLong = false;
			} else if (toBeParsedLength == maxLongLength) {
				// still not sure we can parse
				// could be same length and superior to MAX_LONG
				if (toBeParsed.compareTo(Long.toString(Long.MAX_VALUE)) > 0) {
					// too big
					parseableToInt = false;
					parseableToLong = false;
				} else { // parse to long
					parseableToInt = false;
					parseableToLong = true;
				}
			} else { // inferior length => parse to long
				// not managed : could sometimes be parsed to int
				parseableToInt = false;
				parseableToLong = true;
			}
		} else { // parse to int
			parseableToInt = true;
			parseableToLong = true;
		}

		if (parseableToInt) {
			return Parseability.PARSEABLE_INT;
		} else if (parseableToLong) {
			return Parseability.PARSEABLE_LONG;
		} else {
			return Parseability.NOT_PARSEABLE;
		}
	}

}
