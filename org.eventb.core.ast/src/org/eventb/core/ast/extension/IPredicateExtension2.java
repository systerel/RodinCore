/*******************************************************************************
 * Copyright (c) 2010, 2022 Systerel and others.
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
 * Common protocol for predicate extensions.
 * 
 * This interface adds the <code>verifyType</code> method for better
 * performance. It should be used instead of <code>IPredicateExtension</code>
 * whenever possible.
 *
 * @author Laurent Voisin
 * @since 3.6
 */
public interface IPredicateExtension2 extends IPredicateExtension {

	/**
	 * Verifies that the type of the given children is coherent with the semantics
	 * of this predicate extension. It is guaranteed that all children are
	 * well-typed and that their types are compatible with each other.
	 * 
	 * @param childExprs the child expressions
	 * @param childPreds the child predicates
	 * @return <code>true</code> iff the children types are coherent,
	 *         <code>false</code> otherwise
	 */
	boolean verifyType(Expression[] childExprs, Predicate[] childPreds);

}
