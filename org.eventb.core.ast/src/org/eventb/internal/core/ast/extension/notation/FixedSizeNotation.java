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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eventb.core.ast.extension.notation.IFormulaChild;
import org.eventb.core.ast.extension.notation.INotation;
import org.eventb.core.ast.extension.notation.INotationElement;
import org.eventb.core.ast.extension.notation.IFormulaChild.Kind;

/**
 * @author Nicolas Beauger
 *
 */
public class FixedSizeNotation implements INotation {

	private final List<INotationElement> elements;
	private boolean mapped = false;

	public FixedSizeNotation(List<INotationElement> elements) {
		this.elements = new ArrayList<INotationElement>(elements);
	}
	
	public Iterator<INotationElement> iterator() {
		assert mapped;
		return elements.iterator();
	}

	public boolean mapTo(int numExpressions, int numPredicates) {
		int notationExprs = 0;
		int notationPreds = 0;
		for (INotationElement element : elements) {
			if (element instanceof IFormulaChild) {
				IFormulaChild child = (IFormulaChild) element;
				final Kind kind = child.getKind();
				switch(kind) {
				case EXPRESSION:
					notationExprs++;
					break;
				case PREDICATE:
					notationPreds++;
					break;
				}
			}
		}
		if (!(notationExprs == numExpressions && notationPreds == numPredicates)) {
			return false;
		}
		mapped = true;
		return true;
	}

	public boolean isFlattenable() {
		return false;
	}

}
