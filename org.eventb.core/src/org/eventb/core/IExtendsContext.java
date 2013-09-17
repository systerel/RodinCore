/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for an extends clauses of a context.
 * <p>
 * An extends element refers to a context with a name that is accessed and manipulated by
 * {@link IExtendsContext#getAbstractContextName()} and 
 * {@link IExtendsContext#setAbstractContextName(String, IProgressMonitor)}. The method
 * {@link IExtendsContext#getAbstractSCContext()} returns directly a handle
 * to a statically checked context.
 * </p>
 * <p>
 * This interface provides methods for accessing and manipulating the name of
 * the abstract context that is extended by the context containing this element.
 * </p>
 * <p>
 * We call the context that is extended by this context, the
 * <em>abstract context</em>. This is to avoid confusion caused by using the
 * term <em>extended context</em> which could be either context in an
 * extension relationship.
 * </p>
 * <p>
 * The attribute storing the assignment string is <i>optional</i>. This means if the attribute
 * is not present, the value should be interpreted as <i>undefined</i>.
 * </p>
 *
 * @see org.rodinp.core.IRodinElement#getElementName()
 * 
 * @author Stefan Hallerstede
 * 
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IExtendsContext extends IInternalElement {

	IInternalElementType<IExtendsContext> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".extendsContext"); //$NON-NLS-1$

	/**
	 * Tests whether the abstract context name is defined or not.
	 * 
	 * @return whether the abstract context name is defined or not
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	boolean hasAbstractContextName() throws RodinDBException;
	
	/**
	 * Returns the name of the context which is extended by the context which
	 * contains this element.
	 * 
	 * @return the name of the abstract context
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	String getAbstractContextName() throws RodinDBException;

	/**
	 * Returns a handle to the root of the unchecked version of the context
	 * which is extended by the context which contains this element.
	 * 
	 * @return the root of the abstract context
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IContextRoot getAbstractContextRoot() throws RodinDBException;

	/**
	 * Sets the name of a context which is extended by the context which
	 * contains this element.
	 * 
	 * @param name
	 *            the name of the abstract context
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setAbstractContextName(String name, IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns a handle to the checked version of the abstract context, that is
	 * the file produced when statically checking that context.
	 * 
	 * @return a handle to the checked version of the abstract context
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCContextRoot getAbstractSCContext() throws RodinDBException;

}
