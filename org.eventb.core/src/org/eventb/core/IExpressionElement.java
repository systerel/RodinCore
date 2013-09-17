/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B elements that contain an expression.
 * <p>
 * The expression is manipulated as a bare string of characters, as there is no
 * guarantee that it is parseable.
 * </p>
 * <p>
 * The attribute storing the expression string is <i>optional</i>. This means if the attribute
 * is not present, the value should be interpreted as <i>undefined</i>.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IExpressionElement extends IInternalElement {

	/**
	 * Tests whether the expression string is defined or not.
	 * 
	 * @return whether the assignemnt string is defined or not
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	boolean hasExpressionString() throws RodinDBException;
	
	/**
	 * Returns the string representation of the expression contained in this
	 * element.
	 * 
	 * @return the expression of this element as a string
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	String getExpressionString() throws RodinDBException;

	/**
	 * Sets the string representation of the expression contained in this
	 * element.
	 * 
	 * @param expression
	 *            the string representation of the expression
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setExpressionString(String expression, IProgressMonitor monitor) throws RodinDBException;

}
