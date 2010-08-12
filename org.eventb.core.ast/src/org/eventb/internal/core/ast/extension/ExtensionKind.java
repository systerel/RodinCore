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

import static org.eventb.internal.core.ast.extension.OperatorProperties.makeOperProps;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.ITypeDistribution;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IOperatorProperties;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;

public class ExtensionKind implements IExtensionKind {

	private final IOperatorProperties operProps;

	public ExtensionKind(Notation notation, FormulaType formulaType,
			ITypeDistribution childTypes, boolean isAssociative) {
		this.operProps = makeOperProps(notation, formulaType, childTypes,
				isAssociative);
	}

	@Override
	public IOperatorProperties getProperties() {
		return operProps;
	}

	@Override
	public boolean checkPreconditions(Expression[] childExprs,
			Predicate[] childPreds) {
		final ITypeDistribution childTypes = operProps.getChildTypes();
		return childTypes.getExprArity().check(childExprs.length)
				&& childTypes.getPredArity().check(childPreds.length);
	}
}