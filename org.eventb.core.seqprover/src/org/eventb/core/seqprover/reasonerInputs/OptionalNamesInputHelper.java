/*******************************************************************************
 * Copyright (c) 2024, 2025 UPEC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     UPEC - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.reasonerInputs;

import static java.util.Arrays.stream;

import org.eventb.core.ast.FormulaFactory;

/**
 * Helper class for reasoner inputs that need user-provided identifier names.
 *
 * @author Guillaume Verdier
 * @since 3.7
 */
public class OptionalNamesInputHelper {

	public static final String[] NO_NAMES = new String[0];

	/**
	 * Extracts a list of names from user input.
	 *
	 * The names have to be separated by a comma.
	 *
	 * The returned array may be empty, but not {@code null}.
	 *
	 * @param input to split
	 * @return list of names
	 */
	public static String[] splitInput(String input) {
		if (input.isBlank()) {
			return NO_NAMES;
		}
		return stream(input.split(",")).map(String::strip).toArray(String[]::new);
	}

	/**
	 * Checks if a list of names is valid in a given formula factory.
	 *
	 * @param inputs input names to check
	 * @param ff formula factory to use
	 * @return an error message or {@code null} if they are all valid
	 */
	public static String checkNamesValidity(String[] inputs, FormulaFactory ff) {
		String[] invalids = stream(inputs).filter(name -> !isValidName(name, ff)).toArray(String[]::new);
		if (invalids.length == 0) {
			return null;
		} else {
			String invalid = invalids[0];
			if (invalid.length() > 15) {
				invalid = invalid.substring(0, 15) + "...";
			}
			return "Input \"" + invalid + "\" is not a valid identifier: "
					+ "global input should be empty or have a list of comma-separated identifiers";
		}
	}

	private static boolean isValidName(String name, FormulaFactory ff) {
		return ff.isValidIdentifierName(name) && !ff.makeFreeIdentifier(name, null).isPrimed();
	}

}
