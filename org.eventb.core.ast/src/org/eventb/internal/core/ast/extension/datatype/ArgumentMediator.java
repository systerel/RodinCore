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

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IExpressionExtension;
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
	public ArgumentType newArgumentType(ITypeParameter type) {
		return new ArgTypeParamRef(type);
	}

	@Override
	public Argument newArgument(IArgumentType type) {
		return new Argument((ArgumentType) type);
	}

	@Override
	public Argument newArgument(String destructorName, IArgumentType type) {
		return new Argument(destructorName, (ArgumentType) type);
	}

	@Override
	public ArgumentType makePowerSetType(IArgumentType arg) {
		return new ArgPowerSet((ArgumentType) arg);
	}

	@Override
	public ArgumentType makeProductType(IArgumentType left, IArgumentType right) {
		return new ArgProduct((ArgumentType) left, (ArgumentType) right);
	}

	@Override
	public ArgumentType makeRelationalType(IArgumentType left,
			IArgumentType right) {
		return new ArgPowerSet(new ArgProduct((ArgumentType) left,
				(ArgumentType) right));
	}

	@Override
	public ArgumentType makeParametricType(IExpressionExtension typeConstr,
			List<IArgumentType> types) {
		return iMakeParametricType(typeConstr, internalize(types));
	}

	private List<ArgumentType> internalize(List<? extends IArgumentType> types) {
		final int size = types.size();
		final List<ArgumentType> result = new ArrayList<ArgumentType>(size);
		for (final IArgumentType type : types) {
			result.add((ArgumentType) type);
		}
		return result;
	}

	public ArgumentType iMakeParametricType(IExpressionExtension typeConstr,
			List<ArgumentType> types) {
		return new ArgParametricType(typeConstr, internalize(types));
	}

}
