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

import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;

/**
 * Common protocol for the definition of type distributions.
 * <p>
 * A type distribution represents a sequence of formula types. These types can
 * be mixed when the sequence is finite. The sequence may be infinite when it is
 * made of only one type.
 * </p>
 * <p>
 * Instances of this interface can be obtained by calling one of the methods in
 * {@link ExtensionFactory} or by referencing predefined constants.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ITypeDistribution {

	/**
	 * Returns the arity for expressions in this distribution.
	 * 
	 * @return an arity
	 */
	IArity getExprArity();

	/**
	 * Returns the arity for predicates in this distribution.
	 * 
	 * @return an arity
	 */
	IArity getPredArity();

	/**
	 * Returns whether the proposed formulae are compliant with this type
	 * distribution.
	 * 
	 * <p>
	 * As the order is taken into account, it is a stronger verification than
	 * {@link IExtensionKind#checkPreconditions(Expression[], Predicate[])}.
	 * </p>
	 * 
	 * @param formulae
	 *            a list of formulae
	 * @return <code>true</code> iff the given formulae comply with this type
	 *         distribution
	 */
	boolean check(List<? extends Formula<?>> formulae);

	/**
	 * Makes a list of formulae that complies with this type distribution, out
	 * of the given expressions and predicates.
	 * 
	 * @param exprs
	 *            an array of expressions (potentially empty)
	 * @param preds
	 *            an array of expressions (potentially empty)
	 * @return a list of formulae (potentially empty)
	 */
	List<Formula<?>> makeList(Expression[] exprs, Predicate[] preds);
}
