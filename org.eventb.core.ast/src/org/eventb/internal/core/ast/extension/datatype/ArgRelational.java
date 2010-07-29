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
package org.eventb.internal.core.ast.extension.datatype;

import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.datatype.IArgumentType;

/**
 * @author Nicolas Beauger
 *
 */
public class ArgRelational implements IArgumentType {
	
	private final IArgumentType left;
	private final IArgumentType right;

	
	public ArgRelational(IArgumentType left, IArgumentType right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public Type toType(ITypeMediator mediator, TypeInstantiation instantiation) {
		final Type leftType = left.toType(mediator, instantiation);
		final Type rightType = right.toType(mediator, instantiation);
		return mediator.makeRelationalType(leftType, rightType);
	}

	@Override
	public boolean verifyType(Type proposedType, TypeInstantiation instantiation) {
		final Type leftType = proposedType.getSource();
		final Type rightType = proposedType.getTarget();
		if (leftType == null || rightType == null) {
			return false;
		}
		return left.verifyType(leftType, instantiation)
				&& right.verifyType(rightType, instantiation);
	}

}
