/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;

/**
 * Utility class for ensuring that several conditions are met.
 * 
 * @author Laurent Voisin
 */
public class FormulaChecks {

	public static void ensureTagInRange(int tag, int start, int length) {
		if (tag < start || tag >= start + length) {
			throw new IllegalArgumentException("Invalid tag " + tag
					+ " should be in " + start + ".." + (start + length - 1));
		}
	}

	public static void ensureHasType(Expression expr, Type type) {
		if (type != null && type != expr.getType()) {
			throw new IllegalArgumentException("Invalid type " + type
					+ " for expression " + expr);
		}
	}

	public static void ensureValidIdentifierName(String name, FormulaFactory ff) {
		if (!ff.isValidIdentifierName(name)) {
			throw new IllegalArgumentException("Invalid identifier name: "
					+ name);
		}
	}

	public static void ensureMinLength(Object[] array, int minLength) {
		if (array.length < minLength) {
			throw new IllegalArgumentException("Array of length "
					+ array.length + " is too small (at least " + minLength
					+ " required)");
		}
	}

	public static void ensureSameLength(Object[] left, Object[] right) {
		if (left.length != right.length) {
			throw new IllegalArgumentException("Mismatched sizes: "
					+ left.length + " vs " + right.length);
		}
	}
}
