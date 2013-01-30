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

import static org.eventb.core.ast.extension.ExtensionFactory.makeArity;
import static org.eventb.core.ast.extension.ExtensionFactory.makeFixedArity;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;

/**
 * Common protocol for operator properties.
 * 
 * @author Nicolas Beauger
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IOperatorProperties {

	/**
	 * Represents the relative position of an operator with respect to its
	 * arguments.
	 */
	enum Notation {
		/**
		 * The operator is before all its arguments.
		 */
		PREFIX,
		/**
		 * The operator is in between its arguments. Consequently, the operator
		 * must be binary.
		 */
		INFIX,
		/**
		 * The operator is after its arguments.
		 */
		POSTFIX,
	}

	/**
	 * The operator does not take any argument.
	 */
	IArity NULLARY = makeFixedArity(0);

	/**
	 * The operator takes exactly one argument.
	 */
	IArity UNARY = makeFixedArity(1);

	/**
	 * The operator takes exactly two arguments.
	 */
	IArity BINARY = makeFixedArity(2);

	/**
	 * The operator takes at least two arguments.
	 */
	IArity MULTARY_2 = makeArity(2, IArity.MAX_ARITY);

	enum FormulaType {
		EXPRESSION {
			@Override
			public boolean check(Formula<?> formula) {
				return formula instanceof Expression;
			}
		},
		PREDICATE {
			@Override
			public boolean check(Formula<?> formula) {
				return formula instanceof Predicate;
			}
		};

		public abstract boolean check(Formula<?> formula);
	}

	/**
	 * Returns the relative position of an operator with respect to its
	 * arguments (prefix, infix or postfix).
	 * 
	 * @return the relative position of an operator
	 */
	Notation getNotation();

	/**
	 * Returns the kind of formula that this operator builds: expression or
	 * predicate.
	 * 
	 * @return the kind of formula of this operator
	 */
	FormulaType getFormulaType();

	/**
	 * Returns the kind of children that this operator takes.
	 * 
	 * @return the kind of children of this operator
	 */
	ITypeDistribution getChildTypes();

	/**
	 * Tells whether this operator is associative.
	 * 
	 * @return <code>true</code> iff this operator is associative
	 */
	// FIXME clarify relation with the associativity property, set through
	// addCompatibilities; maybe remove this method
	boolean isAssociative();

}
