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
 * This interface extends {@link IVisitor} to add support for predicate
 * variables.
 * 
 * @see Formula#accept(IVisitor)
 * @see DefaultVisitor
 * 
 * @author Thomas Muller
 * @since 1.2
 */
public interface IVisitor2 extends IVisitor {

	/**
	 * Visits a <code>PREDICATE_VARIABLE</code> node.
	 * 
	 * @param predVar
	 *            the node to visit
	 * @return <code>false</code> to prevent visiting the siblings of the given
	 *         node, <code>true</code> to continue visiting
	 * 
	 * @see Formula#accept(IVisitor)
	 */
	boolean visitPREDICATE_VARIABLE(PredicateVariable predVar);

}