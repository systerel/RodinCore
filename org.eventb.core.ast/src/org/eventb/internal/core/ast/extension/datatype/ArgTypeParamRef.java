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
import org.eventb.core.ast.extension.IExpressionExtension;
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
	public Type toType(ITypeMediator mediator, IExpressionExtension typeExtn, TypeParamInst instantiation) {
		return instantiation.get(typeParam);
	}
	
}
