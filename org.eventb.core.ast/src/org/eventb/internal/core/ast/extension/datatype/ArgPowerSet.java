/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension.datatype;

import static org.eventb.core.ast.Formula.POW;

import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.datatype.ITypeParameter;

/**
 * @author Nicolas Beauger
 *
 */
public class ArgPowerSet extends ArgumentType {

	private final ArgumentType arg;

	public ArgPowerSet(ArgumentType arg) {
		this.arg = arg;
	}
	
	@Override
	public Type toType(ITypeMediator mediator, TypeInstantiation instantiation) {
		final Type type = arg.toType(mediator, instantiation);
		return mediator.makePowerSetType(type);
	}

	@Override
	public boolean verifyType(Type proposedType, TypeInstantiation instantiation) {
		final Type baseType = proposedType.getBaseType();
		if (baseType == null) {
			return false;
		}
		return arg.verifyType(baseType, instantiation);
	}

	@Override
	public Expression toSet(FormulaFactory factory,
			Map<ITypeParameter, Expression> substitution) {
		final Expression argSet = arg.toSet(factory, substitution);
		return factory.makeUnaryExpression(POW, argSet, null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arg == null) ? 0 : arg.hashCode());
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
		if (!(obj instanceof ArgPowerSet)) {
			return false;
		}
		ArgPowerSet other = (ArgPowerSet) obj;
		if (arg == null) {
			if (other.arg != null) {
				return false;
			}
		} else if (!arg.equals(other.arg)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "'ℙ(" + arg + ")";
	}

}
