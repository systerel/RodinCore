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
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for predicate elements that can be generated.
 * <p>
 * The generated indication is stored in the database as an optional attribute.
 * If the attribute is absent, it is deemed to be <code>false</code>. The query
 * methods in this interface implement directly this protocol.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @noimplement
 * 
 * @author "Nicolas Beauger"
 * @since 1.0
 */
public interface IGeneratedElement {

	/**
	 * Returns <code>true</code>. As the generated attribute is always
	 * considered present, this method always returns <code>true</code>, whether
	 * the attribute actually exists or not.
	 * 
	 * @return <code>true</code>
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	boolean hasGenerated() throws RodinDBException;

	/**
	 * Returns whether this element is a generated element.
	 * 
	 * @return <code>true</code> iff this element is a generated element
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	boolean isGenerated() throws RodinDBException;

	/**
	 * Sets whether this element is a generated element.
	 * 
	 * @param generated
	 *            the new value of the generated attribute
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setGenerated(boolean generated, IProgressMonitor monitor)
			throws RodinDBException;

}