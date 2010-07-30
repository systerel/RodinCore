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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.datatype.IArgument;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.IDatatypeMediator;
import org.eventb.core.ast.extension.datatype.ITypeParameter;
import org.eventb.internal.core.ast.extension.TypeMediator;

/**
 * @author Nicolas Beauger
 * 
 */
public class DatatypeMediator extends TypeMediator implements IDatatypeMediator {

	protected final List<ITypeParameter> typeParams;
	protected final Set<IExpressionExtension> extensions = new HashSet<IExpressionExtension>();

	public DatatypeMediator(List<ITypeParameter> typeParams, FormulaFactory factory) {
		super(factory);
		this.typeParams = typeParams;
	}

	@Override
	public ITypeParameter getTypeParameter(String name) {
		for (ITypeParameter param : typeParams) {
			if (param.getName().equals(name)) {
				return param;
			}
		}
		return null;
	}

	@Override
	public IArgumentType newArgumentType(ITypeParameter type) {
		return new ArgTypeParamRef(type);
	}

	@Override
	public IArgumentType newArgumentType(Type type) {
		return new ArgSimpleType(type);
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
		return new ArgRelational(left, right);
	}

	@Override
	public IArgumentType newArgumentTypeConstr(List<IArgumentType> types) {
		return new ArgGenTypeRef(types);
	}

	public Set<IExpressionExtension> getExtensions() {
		return Collections.unmodifiableSet(extensions);
	}
}
