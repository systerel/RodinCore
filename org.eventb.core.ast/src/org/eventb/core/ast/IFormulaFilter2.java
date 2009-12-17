/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

/**
 * This interface extends {@link IFormulaFilter} to add support for predicate
 * variables.
 * 
 * @see Formula#getPositions(IFormulaFilter)
 * 
 * @author Thomas Muller
 * @since 1.2
 */
public interface IFormulaFilter2 extends IFormulaFilter {

	/**
	 * Tells whether the given predicate variable passes this filter criterion.
	 * 
	 * @param predVar
	 *            predicate variable to test
	 * @return <code>true</code> iff the given predicate variable passes the
	 *         criterion
	 */
	boolean select(PredicateVariable predVar);

}
