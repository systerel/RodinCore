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

/**
 * TODO instead of encapsulating children, pass the parent node as argument to
 * extension methods
 * 
 * @author Nicolas Beauger
 */
/* package */class ToStringMediator extends ToStringFullParenMediator {

	private final int tag;
	private final boolean withTypes;

	public ToStringMediator(StringBuilder builder, int tag,
			String[] boundNames, boolean withTypes) {
		super(builder, boundNames);
		this.tag = tag;
		this.withTypes = withTypes;
	}

	@Override
	public void append(Formula<?> child, boolean isRight) {
		child.toString(builder, isRight, tag, boundNames, withTypes);
	}
}
