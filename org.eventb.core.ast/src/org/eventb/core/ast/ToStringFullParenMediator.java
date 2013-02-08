/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
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
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.parser.AbstractGrammar;

/**
 * @author Nicolas Beauger
 */
/* package */class ToStringFullParenMediator extends ToStringMediator  {


	public ToStringFullParenMediator(Formula<?> formula, StringBuilder builder,
			String[] boundNames, boolean isRight) {
		super(formula, builder, boundNames, isRight, false);
	}

	protected ToStringFullParenMediator(int formulaKind,
			AbstractGrammar grammar, StringBuilder builder,
			String[] boundNames, boolean isRight, KindMediator kindMed) {
		super(formulaKind, grammar, builder, boundNames, isRight, false,
				kindMed);
	}

	@Override
	protected IToStringMediator makeInstance(int formulaKind,
			boolean isRightOvr, boolean withTypes, String[] newBoundNames) {
		return new ToStringFullParenMediator(formulaKind, grammar, builder,
				newBoundNames, isRightOvr, kindMed);
	}

	@Override
	protected boolean needsParentheses(int childKind, boolean isRightOvr) {
		return true;
	}
}
