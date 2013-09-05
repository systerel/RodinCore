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
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IOperatorProperties;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.ITypeDistribution;

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

	@Override
	public boolean checkTypePreconditions(Type[] typeParameters) {
		final ITypeDistribution childTypes = operProps.getChildTypes();
		// Predicate arity has been checked at factory creation
		return childTypes.getExprArity().check(typeParameters.length);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((operProps == null) ? 0 : operProps.hashCode());
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
		if (!(obj instanceof ExtensionKind)) {
			return false;
		}
		ExtensionKind other = (ExtensionKind) obj;
		if (operProps == null) {
			if (other.operProps != null) {
				return false;
			}
		} else if (!operProps.equals(other.operProps)) {
			return false;
		}
		return true;
	}
	
}