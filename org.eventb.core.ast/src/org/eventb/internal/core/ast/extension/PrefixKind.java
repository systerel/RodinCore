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
package org.eventb.internal.core.ast.extension;

import static org.eventb.core.ast.extension.IOperatorProperties.Notation.PREFIX;

import org.eventb.core.ast.extension.IOperatorProperties.FixedArity;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;

public class PrefixKind extends ExtensionKind {

	public PrefixKind(FormulaType formulaType, int arity,
			FormulaType argumentType) {
		super(PREFIX, formulaType, new FixedArity(arity), argumentType, false);
	}

}