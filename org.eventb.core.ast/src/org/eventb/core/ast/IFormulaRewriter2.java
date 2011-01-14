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
 * This interface extends {@link IFormulaRewriter} to add support for predicate
 * variables.
 * 
 * @see Formula#rewrite(IFormulaRewriter)
 * @see DefaultRewriter
 * 
 * @author Thomas Muller
 * @since 1.2
 */
public interface IFormulaRewriter2 extends IFormulaRewriter {

	/**
	 * Rewrites the given predicate variable.
	 * 
	 * @param predVar
	 *            predicate variable to rewrite
	 * @return the given predicate variable rewritten
	 */
	Predicate rewrite(PredicateVariable predVar);
	
}
