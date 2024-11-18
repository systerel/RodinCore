/*******************************************************************************
 * Copyright (c) 2013, 2024 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.datatype;

import static org.eventb.core.ast.extension.ExtensionFactory.makeAllExpr;
import static org.eventb.core.ast.extension.ExtensionFactory.makeFixedArity;
import static org.eventb.core.ast.extension.ExtensionFactory.makePrefixKind;
import static org.eventb.core.ast.extension.IFormulaExtension.ATOMIC_EXPRESSION;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;
import static org.eventb.core.ast.extension.StandardGroup.ATOMIC_EXPR;
import static org.eventb.core.ast.extension.StandardGroup.CLOSED;

import org.eventb.core.ast.extension.IExtensionKind;

/**
 * This class provides services for building and defining a datatype.
 * <p>
 * It provides the way to have unique ID for datatype extensions, as well as
 * correct group ID and extension kind regarding extensions arguments.
 * </p>
 * 
 * @author Vincent Monfort
 */
public class DatatypeHelper {

	private static long uniqueId = 0;

	public static synchronized String computeId(String name) {
		if (name == null) {
			name = "Id";
		}
		return name + uniqueId++;
	}

	public static String computeGroup(int nbArgs) {
		if (nbArgs > 0) {
			return CLOSED.getId();
		}
		if (nbArgs == 0) {
			return ATOMIC_EXPR.getId();
		}
		throw new IllegalArgumentException("negative number of arguments !");
	}

	public static IExtensionKind computeKind(int nbArgs) {
		final IExtensionKind kind;
		if (nbArgs == 0) {
			kind = ATOMIC_EXPRESSION;
		} else {
			kind = makePrefixKind(EXPRESSION,
					makeAllExpr(makeFixedArity(nbArgs)));
		}
		return kind;
	}

}
