/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
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

	private final String string;
	private final int position;
	private final FormulaFactory ff;
	
	public PrefixComputer(String string, int position, FormulaFactory ff) {
		this.string = string;
		this.position = position;
		this.ff = ff;
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

	private boolean isIdentifierPrefix(String candidate) {
		// a prefix of an identifier is a valid identifier 
		return ff.isValidIdentifierName(candidate);
	}
	
	private boolean isIdentifierSuffix(String candidate) {
		return ff.isValidIdentifierName("P" + candidate);
	}


}