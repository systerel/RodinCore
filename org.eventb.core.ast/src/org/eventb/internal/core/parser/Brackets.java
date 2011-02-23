/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser;

/**
 * Data structure for matching brackets.
 * <p>
 * Matching information is known but not used by the grammar.
 * </p>
 * 
 * @author Nicolas Beauger
 */
public class Brackets {

	// using arrays instead of lists to avoid conversion from int to Integer
	// for all i, open[i] and close[i] are of matching bracket token kinds
	private final int[] open;
	private final int[] close;

	public Brackets(int[] open, int[] close) {
		assert open.length == close.length;
		this.open = open;
		this.close = close;
	}

	public boolean isOpen(int kind) {
		return contains(open, kind);
	}

	public boolean isClose(int kind) {
		return contains(close, kind);
	}

	private static boolean contains(int[] array, int kind) {
		for (int k : array) {
			if (k == kind)
				return true;
		}
		return false;
	}

}