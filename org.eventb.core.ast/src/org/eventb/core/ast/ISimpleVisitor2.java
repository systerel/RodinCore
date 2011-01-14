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
 * This interface extends {@link ISimpleVisitor} to add support for predicate
 * variables.
 * 
 * @see Formula#accept(ISimpleVisitor)
 * @see DefaultSimpleVisitor
 * 
 * @author Thomas Muller
 * @since 1.2
 */
public interface ISimpleVisitor2 extends ISimpleVisitor {

	/**
	 * Visits the given predicate variable node.
	 * 
	 * @param predVar
	 *            the predicate variable node to visit
	 */
	void visitPredicateVariable(PredicateVariable predVar);

}
