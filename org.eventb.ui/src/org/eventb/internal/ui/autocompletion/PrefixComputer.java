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

	// linear search from start to end
	private static int getPrefixLinear(String text, int start, int end) {
		for (int pos = start; pos < end; pos++) {
			final String candidate = text.substring(pos, end);
			if (ff.isValidIdentifierName(candidate)) {
				return pos;
			}
		}
		return end;
	}
	
	// search by pitch
	private static int getPrefixPitch(String string, int position) {
		final int pitch = 4;
		if (position < pitch) {
			return getPrefixLinear(string, 0, position);
		}
		final int pitchedPos = position - pitch;
		final String candidate = string.substring(pitchedPos, position);
		if (ff.isValidIdentifierName(candidate)) {
			// any prefix of a prefix is a prefix ("" is considered a prefix)
			// => shift left
			return getPrefixPitch(string, pitchedPos);
		} else {
			return getPrefixLinear(string, pitchedPos, position);
		}
	}
	
	private final String string;
	private final int position;
	
	public PrefixComputer(String string, int position) {
		this.string = string;
		this.position = position;
	}
	
	public String getPrefix() {
		final int prefixPos = getPrefixPitch(string, position);
		return string.substring(prefixPos, position);
	}

}