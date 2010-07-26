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
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.IDatatypeMediator;
import org.eventb.core.ast.extension.datatype.ITypeParameter;

/**
 * @author Nicolas Beauger
 * 
 */
public class DatatypeMediator implements IDatatypeMediator {

	protected final Map<String, ITypeParameter> typeParams;
	protected final Set<IExpressionExtension> extensions = new HashSet<IExpressionExtension>();

	public DatatypeMediator(Map<String, ITypeParameter> typeParams) {
		this.typeParams = typeParams;
	}

	@Override
	public ITypeParameter getTypeParameter(String name) {
		return typeParams.get(name);
	}

	@Override
	public IArgumentType newArgumentType(ITypeParameter type) {
		return new ArgTypeParamRef(type);
	}

	@Override
	public IArgumentType newArgumentTypeConstr(List<IArgumentType> types) {
		return new ArgGenTypeRef(types);
	}

	public Set<IExpressionExtension> getExtensions() {
		return Collections.unmodifiableSet(extensions);
	}
}
