/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.autocompletion;

import org.eventb.core.ast.FormulaFactory;

public class PrefixComputer {

	// TODO see if we can factorize a unique FormulaFactory for the plugin
	private static final FormulaFactory ff = FormulaFactory.getDefault();

	private static boolean isIdentifierPrefix(String string) {
		// a prefix of an identifier is a valid identifier 
		return ff.isValidIdentifierName(string);
	}
	
	private static boolean isIdentifierSuffix(String string) {
		return ff.isValidIdentifierName("P" + string);
	}

	private final String string;
	private final int position;
	
	public PrefixComputer(String string, int position) {
		this.string = string;
		this.position = position;
	}
	
	public String getPrefix() {
		final int suffixPos = getLowestSuffixPos();
		
		final String candidate = string.substring(suffixPos, position);
		if (isIdentifierPrefix(candidate)) {
			return candidate;
		} else {
			return "";
		}
	}

	private int getLowestSuffixPos() {
		int pos;
		for (pos = position; pos > 0; pos--) {
			final String suffix = string.substring(pos-1, position);
			if (!isIdentifierSuffix(suffix)) {
				return pos;
			}
		}
		return pos;
	}

}