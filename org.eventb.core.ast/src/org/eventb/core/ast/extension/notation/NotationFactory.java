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
package org.eventb.core.ast.extension.notation;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.extension.notation.IFormulaChild.Kind;
import org.eventb.internal.core.ast.extension.notation.FormulaChild;
import org.eventb.internal.core.ast.extension.notation.FixedSizeNotation;
import org.eventb.internal.core.ast.extension.notation.NotationSymbol;
import org.eventb.internal.core.ast.extension.notation.VariableSizeNotation;

/**
 * @author Nicolas Beauger
 * @since 2.0
 *
 */
public class NotationFactory {

	private static final NotationFactory INSTANCE = new NotationFactory();
	
	private NotationFactory() {
		// singleton
	}
	
	public static NotationFactory getInstance() {
		return INSTANCE;
	}
	
	public INotation makeNotation(String syntaxSymbol,
			INotationElement... elements) {
		return new FixedSizeNotation(syntaxSymbol, Arrays.asList(elements));
	}

	public INotation makeNotation(String syntaxSymbol,
			List<INotationElement> elements) {
		return new FixedSizeNotation(syntaxSymbol, elements);
	}

	public INotation makeAssociativeInfixNotation(INotationSymbol symbol, Kind kind) {
		return new VariableSizeNotation(symbol, kind);
	}
	
	public INotationSymbol makeSymbol(String symbol) {
		return new NotationSymbol(symbol);
	}
	
	public IFormulaChild makeChild(int index, Kind kind) {
		return new FormulaChild(index, kind);
	}
	
}
