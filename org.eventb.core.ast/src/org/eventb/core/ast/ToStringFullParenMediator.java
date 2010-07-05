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
package org.eventb.core.ast;

import org.eventb.internal.core.ast.extension.IToStringMediator;

/**
 * @author Nicolas Beauger
 */
/* package */class ToStringFullParenMediator extends ToStringMediator  {


	public ToStringFullParenMediator(FormulaFactory factory, int tag,
			StringBuilder builder, String[] boundNames, boolean isRight) {
		super(factory, builder, boundNames, tag, false, isRight);
	}

	@Override
	protected IToStringMediator makeInstance(Formula<?> child,
			boolean isRightOvr, boolean withTypes, String[] newBoundNames) {
		return new ToStringFullParenMediator(factory, child.getTag(), builder,
				newBoundNames, isRightOvr);
	}

	@Override
	protected boolean needsParentheses(Formula<?> child, boolean isRightOvr) {
		return true;
	}
}
