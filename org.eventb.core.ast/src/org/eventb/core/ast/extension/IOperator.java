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

/**
 * Common protocol for operators.
 * <p>
 * Instances of this interface are provided by
 * {@link IOperatorGroup#getOperators()} and other related methods. The purpose
 * is to give a view of an operator. Objects returned by the various methods are
 * not intended to be modified. Anyway, modifications would have no effect on
 * the described operator.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IOperator {

	/**
	 * Returns the symbol of this operator, as used in String formulae.
	 * 
	 * @return a String symbol
	 */
	String getSyntaxSymbol();

	/**
	 * Returns the id of this operator
	 * 
	 * @return a String identifier
	 */
	String getId();

}
