/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B statically checked (SC) context files.
 * <p>
 * An SC context file has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}.
 * </p>
 * <p>
 * The elements contained in an event-B SC context are:
 * <ul>
 * <li>internal contexts (<code>ISCInternalContext</code>)</li>
 * <li>carrier sets (<code>ISCCarrierSet</code>)</li>
 * <li>constants (<code>ISCConstant</code>)</li>
 * <li>axioms (<code>ISCAxiom</code>)</li>
 * <li>theorems (<code>ISCTheorem</code>)</li>
 * </ul>
 * </p>
 * <p>
 * The internal contexts are a local copy of the contents of the abstract
 * contexts of this context, i.e., the contexts which are, directly or
 * indirectly, extended by this context. The other child elements of this
 * context are the SC versions of the elements of the unchecked version of this
 * context. They are manipulated by means of {@link org.eventb.core.ISCContext}.
 * In addition, access methods for related file handles are provided.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * 
 * @author Stefan Hallerstede
 */
public interface ISCContextFile extends ISCContext, IRodinFile {

	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".scContextFile"; //$NON-NLS-1$

	/**
	 * Returns a handle to the unchecked version of this context.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the unchecked version of this context
	 */
	IContextFile getContextFile();

	/**
	 * Returns the internal SC contexts that are (transitively) contained in,
	 * i.e. extended by, this SC context.
	 * 
	 * @return an array of all internal contexts
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCInternalContext[] getAbstractSCContexts() throws RodinDBException;

}
