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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ArgRelational)) {
			return false;
		}
		ArgRelational other = (ArgRelational) obj;
		if (left == null) {
			if (other.left != null) {
				return false;
			}
		} else if (!left.equals(other.left)) {
			return false;
		}
		if (right == null) {
			if (other.right != null) {
				return false;
			}
		} else if (!right.equals(other.right)) {
			return false;
		}
		return true;
	}

}
