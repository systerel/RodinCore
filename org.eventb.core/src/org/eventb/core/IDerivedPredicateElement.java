/*******************************************************************************
 * Copyright (c) 2009, 2013 Universitaet Duesseldorf and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Universitaet Duesseldorf - initial API and implementation
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for predicate elements that can be theorems.
 * <p>
 * The theorem indication is stored in the database as an optional attribute. If
 * the attribute is absent, it is deemed to be <code>false</code>. The query
 * methods in this interface implement directly this protocol.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IDerivedPredicateElement extends IPredicateElement {

	/**
	 * Returns whether this element is a theorem.
	 * 
	 * @return whether this element is a theorem
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	boolean isTheorem() throws RodinDBException;

	/**
	 * Sets whether this element is a theorem.
	 * 
	 * @param thm
	 *            the new value of the theorem attribute
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setTheorem(boolean thm, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns <code>true</code>. As the theorem attribute is always considered
	 * present, this method always returns <code>true</code>, whether the
	 * attribute actually exists or not.
	 * 
	 * @return <code>true</code>
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	boolean hasTheorem() throws RodinDBException;
}
