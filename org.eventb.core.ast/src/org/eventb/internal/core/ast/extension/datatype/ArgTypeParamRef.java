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
import org.eventb.core.ast.extension.datatype.ITypeParameter;

/**
 * @author Nicolas Beauger
 *
 */
public class ArgTypeParamRef implements IArgumentType {

	private final ITypeParameter typeParam;

	public ArgTypeParamRef(ITypeParameter type) {
		this.typeParam = type;
	}

	@Override
	public Type toType(ITypeMediator mediator, TypeInstantiation instantiation) {
		return instantiation.get(typeParam);
	}

	@Override
	public boolean verifyType(Type proposedType, TypeInstantiation instantiation) {
		final Type instType = instantiation.get(typeParam);
		return proposedType.equals(instType);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((typeParam == null) ? 0 : typeParam.hashCode());
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
		if (!(obj instanceof ArgTypeParamRef)) {
			return false;
		}
		ArgTypeParamRef other = (ArgTypeParamRef) obj;
		if (typeParam == null) {
			if (other.typeParam != null) {
				return false;
			}
		} else if (!typeParam.equals(other.typeParam)) {
			return false;
		}
		return true;
	}
	
}
