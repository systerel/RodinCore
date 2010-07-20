/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added method hasProblem()
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.List;

/**
 * Common protocol for reporting results of AST operations.
 * <p>
 * Every operation that can produce error messages, returns an instance of this
 * interface that collects the result status of the operation. This object can
 * then be queried using the methods defined in this interface.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IResult {

	/**
	 * Returns whether a problem was encountered during the operation. This is a
	 * short-hand, fully equivalent to <code>!getProblems().isEmpty()</code>.
	 * 
	 * @return whether a problem occurred
	 */
	boolean hasProblem();

	/**
	 * Returns a list of all the problems encountered during the operation.
	 * 
	 * @return a list of all problems
	 */
	List<ASTProblem> getProblems();

	/**
	 * Returns whether this result is a success, i.e., doesn't contain any error
	 * problem. Consequently, a result that contains only warnings is considered
	 * as successful.
	 * <p>
	 * Clients who need to know that no problem at all was encountered
	 * (including warnings), should rather use {@link #hasProblem()}.
	 * 
	 * @return <code>true</code> iff the operation succeeded
	 * @see #hasProblem()
	 */
	boolean isSuccess();

}