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
	 */
	public static class Arity {
		private final int min;
		private final int max;
		
		public Arity(int min, int max) {
			this.min = min;
			this.max = max;
		}
		
		public boolean check(int nbArgs) {
			return min <= nbArgs && nbArgs <= max;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = prime + max;
			result = prime * result + min;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Arity)) {
				return false;
			}
			Arity other = (Arity) obj;
			if (max != other.max) {
				return false;
			}
			if (min != other.min) {
				return false;
			}
			return true;
		}
	}
	
	public static class FixedArity extends Arity {
		public FixedArity(int arity) {
			super(arity, arity);
		}
	}
	
	public static final Arity NULLARY = new FixedArity(0);
	public static final Arity UNARY = new FixedArity(1);
	public static final Arity BINARY = new FixedArity(2);
	public static final Arity MULTARY_2 = new Arity(2, UNBOUNDED);
	
	public static enum FormulaType {
		EXPRESSION, PREDICATE
	}

	Notation getNotation();
	
	FormulaType getFormulaType();
	
	// TODO move elsewhere (not a static property)
	Arity getArity();
	
	FormulaType getArgumentType();

}
