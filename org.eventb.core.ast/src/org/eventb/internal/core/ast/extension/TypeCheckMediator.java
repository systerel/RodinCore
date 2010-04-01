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
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.internal.core.typecheck.TypeCheckResult;

/**
 * @author Nicolas Beauger
 */
public class TypeCheckMediator extends TypeMediator implements ITypeCheckMediator {

	private final TypeCheckResult result;
	private final Formula<?> formula;

	public TypeCheckMediator(TypeCheckResult result, Formula<?> formula) {
		super(result.getFormulaFactory());
		this.result = result;
		this.formula = formula;
	}

	public void sameType(Type left, Type right) {
		result.unify(left, right, formula);
	}

}
