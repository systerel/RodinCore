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
package org.eventb.internal.core.ast.extension.notation;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eventb.core.ast.extension.notation.INotation;
import org.eventb.core.ast.extension.notation.INotationElement;
import org.eventb.core.ast.extension.notation.INotationSymbol;
import org.eventb.core.ast.extension.notation.IFormulaChild.Kind;

/**
 * @author Nicolas Beauger
 * 
 */
public class VariableSizeNotation implements INotation {

	private static class NotationIterator implements Iterator<INotationElement> {

		private final int length;
		private final Kind kind;
		private final INotationSymbol symbol;
		private int index = 0;
		private boolean makeChild = true;

		public NotationIterator(int length, Kind kind, INotationSymbol symbol) {
			this.length = length;
			this.kind = kind;
			this.symbol = symbol;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public INotationElement next() {
			if (index >= length) {
				throw new NoSuchElementException();
			}
			final INotationElement result;
			if (makeChild) {
				result = new FormulaChild(index, kind);
				index++;
			} else {
				result = symbol;
			}
			makeChild = !makeChild;
			return result;
		}

		public boolean hasNext() {
			return index < length;
		}
	}

	private final INotationSymbol symbol;
	private final Kind kind;
	private int actualLength = 0;
	private boolean mapped = false;

	public VariableSizeNotation(INotationSymbol symbol, Kind kind) {
		this.symbol = symbol;
		this.kind = kind;
	}

	public Iterator<INotationElement> iterator() {
		assert mapped;
		return new NotationIterator(actualLength, kind, symbol);
	}

	public boolean mapTo(int numExpressions, int numPredicates) {
		if (!(numExpressions == 0 || numPredicates == 0)) {
			return false;
		}
		switch (kind) {
		case EXPRESSION:
			if (numExpressions > 0) {
				actualLength = numExpressions;
			}
			break;
		case PREDICATE:
			if (numPredicates > 0) {
				actualLength = numPredicates;
			}
			break;
		default:
			return false;
		}
		mapped = true;
		return true;
	}

	public boolean isFlattenable() {
		return true;
	}

}
