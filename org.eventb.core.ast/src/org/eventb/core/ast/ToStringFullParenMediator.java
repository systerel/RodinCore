/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

import org.eventb.internal.core.ast.extension.IToStringMediator;

/**
 * @author Nicolas Beauger
 */
/* package */class ToStringFullParenMediator extends ToStringMediator  {


	public ToStringFullParenMediator(Formula<?> formula, FormulaFactory factory,
			StringBuilder builder, String[] boundNames, boolean isRight) {
		super(formula, factory, builder, boundNames, false, isRight);
	}

	public ToStringFullParenMediator(int formulaKind, FormulaFactory factory,
			StringBuilder builder, String[] boundNames, boolean isRight) {
		super(formulaKind, factory, builder, boundNames, false, isRight);
	}

	@Override
	protected IToStringMediator makeInstance(int formulaKind,
			boolean isRightOvr, boolean withTypes, String[] newBoundNames) {
		return new ToStringFullParenMediator(formulaKind, factory, builder,
				newBoundNames, isRightOvr);
	}

	@Override
	protected boolean needsParentheses(int childKind, boolean isRightOvr) {
		return true;
	}
}
