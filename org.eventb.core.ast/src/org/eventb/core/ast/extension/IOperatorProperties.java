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

	enum Notation {
		PREFIX, INFIX, POSTFIX
	}
	
	IArity NULLARY = makeFixedArity(0);
	IArity UNARY = makeFixedArity(1);
	IArity BINARY = makeFixedArity(2);
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

	Notation getNotation();
	
	FormulaType getFormulaType();
	
	ITypeDistribution getChildTypes();

	// FIXME clarify relation with the associativity property, set through
	// addCompatibilities; maybe remove this method
	boolean isAssociative();

}
