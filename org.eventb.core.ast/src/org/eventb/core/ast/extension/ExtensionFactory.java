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
package org.eventb.core.ast.extension;

import static org.eventb.core.ast.extension.IOperatorProperties.Notation.PREFIX;

import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.internal.core.ast.extension.Arity;
import org.eventb.internal.core.ast.extension.ExtensionKind;

/**
 * @author Nicolas Beauger
 * @since 2.0
 * 
 */
public class ExtensionFactory {
	
	// FIXME for now, only EXPRESSION children are supported.
	public static IExtensionKind makePrefixKind(FormulaType formulaType,
			int arity, FormulaType argumentType) {
		return new ExtensionKind(PREFIX, formulaType, makeFixedArity(arity),
				argumentType, false);
	}

	public static IArity makeArity(int min, int max) {
		return new Arity(min, max);
	}

	public static IArity makeFixedArity(int arity) {
		return new Arity(arity, arity);
	}

}
