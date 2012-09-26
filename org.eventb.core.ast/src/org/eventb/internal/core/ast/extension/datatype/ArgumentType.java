/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension.datatype;

import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.ITypeParameter;

/**
 * Common implementation of argument types used for constructors of parametric
 * datatypes.
 * 
 * @author Laurent Voisin
 */
public abstract class ArgumentType implements IArgumentType {

	/**
	 * Returns the set expression corresponding to this argument type where type
	 * parameters have been substituted by the given set expressions.
	 * 
	 * @param factory
	 *            factory for building the result
	 * @param substitution
	 *            substitution of set expressions for type parameters
	 * 
	 * @return this argument type as a set expression where type parameters have
	 *         been substituted
	 */
	public abstract Expression toSet(FormulaFactory factory,
			Map<ITypeParameter, Expression> substitution);

}
