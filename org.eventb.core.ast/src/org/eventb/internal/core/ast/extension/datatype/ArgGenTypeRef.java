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

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.datatype.IArgumentType;

/**
 * @author Nicolas Beauger
 *
 */
public class ArgGenTypeRef implements IArgumentType {

	protected final List<IArgumentType> argTypes;

	public ArgGenTypeRef(List<IArgumentType> argTypes) {
		this.argTypes = argTypes;
	}

	@Override
	public Type toType(ITypeMediator mediator, IExpressionExtension typeExtn,
			TypeParamInst instantiation) {
		assert typeExtn.isATypeConstructor();
		final List<Type> argTypesInst = new ArrayList<Type>();
		for (IArgumentType arg : argTypes) {
			final Type argType = arg.toType(mediator, typeExtn, instantiation);
			argTypesInst.add(argType);
		}
		return mediator.makeGenericType(argTypesInst, typeExtn);
	}
}
