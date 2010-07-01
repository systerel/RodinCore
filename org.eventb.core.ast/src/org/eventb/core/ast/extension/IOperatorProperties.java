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
package org.eventb.core.ast.extension;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;

/**
 * Common protocol for operator properties.
 * 
 * @author Nicolas Beauger
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IOperatorProperties {

	public static enum Notation {
		PREFIX, INFIX, POSTFIX
	}
	
	static final int UNBOUNDED = Integer.MAX_VALUE;

	/**
	 * Arity of an operator.
	 * <p>
	 * Note: for N_ARY arity, select MULTARY_1 then implement/override
	 * {@link IExtensionKind#checkPreconditions(Expression[], Predicate[])} to
	 * check the arity for the desired n.
	 * </p>
	 * FIXME except associative formulae, all are N_ARY (fixed n) 
	 */
	public static enum Arity {
		NULLARY(0, 0), UNARY(1, 1), BINARY(2, 2),
		MULTARY_1(1, UNBOUNDED), MULTARY_2(2, UNBOUNDED);
		
		private final int min;
		private final int max;
		
		private Arity(int min, int max) {
			this.min = min;
			this.max = max;
		}
		
		public boolean check(int nbArgs) {
			return min <= nbArgs && nbArgs <= max;
		}
	}

	public static enum FormulaType {
		EXPRESSION, PREDICATE
	}

	Notation getNotation();
	
	FormulaType getFormulaType();
	
	// TODO move elsewhere (not a static property)
	Arity getArity();
	
	FormulaType getArgumentType();

}
