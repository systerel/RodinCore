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
package org.eventb.internal.core.ast.extension;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.internal.core.typecheck.TypeCheckResult;

/**
 * @author Nicolas Beauger
 */
public class TypeCheckMediator extends TypeMediator implements
		ITypeCheckMediator {

	private final TypeCheckResult result;
	private final Formula<?> formula;

	// used for creating fresh type variables,
	// not <code>null</code> only for typed terminal nodes
	private final SourceLocation location;

	public TypeCheckMediator(TypeCheckResult result, Formula<?> formula,
			boolean isTypedTerminal) {
		super(result.getFormulaFactory());
		this.result = result;
		this.formula = formula;
		this.location = isTypedTerminal ? formula.getSourceLocation() : null;
	}

	@Override
	public Type newTypeVariable() {
		return result.newFreshVariable(location);
	}

	@Override
	public void sameType(Type left, Type right) {
		result.unify(left, right, formula);
	}

}
