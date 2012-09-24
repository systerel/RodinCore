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

import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.datatype.IArgument;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.ITypeParameter;
import org.eventb.internal.core.ast.extension.TypeMediator;

/**
 * @author Nicolas Beauger
 * 
 */
public abstract class ArgumentMediator extends TypeMediator implements
		IConstructorMediator {

	public ArgumentMediator(FormulaFactory factory) {
		super(factory);
	}

	@Override
	public IArgumentType newArgumentType(ITypeParameter type) {
		return new ArgTypeParamRef(type);
	}

	@Override
	public IArgument newArgument(IArgumentType type) {
		return new Argument(type);
	}

	@Override
	public IArgument newArgument(String destructorName, IArgumentType type) {
		return new Argument(destructorName, type);
	}

	@Override
	public IArgumentType makePowerSetType(IArgumentType arg) {
		return new ArgPowerSet(arg);
	}

	@Override
	public IArgumentType makeProductType(IArgumentType left, IArgumentType right) {
		return new ArgProduct(left, right);
	}

	@Override
	public IArgumentType makeRelationalType(IArgumentType left,
			IArgumentType right) {
		return new ArgPowerSet(new ArgProduct(left, right));
	}

	@Override
	public IArgumentType makeParametricType(IExpressionExtension typeConstr,
			List<IArgumentType> types) {
		return new ArgParametricType(typeConstr, types);
	}
}
