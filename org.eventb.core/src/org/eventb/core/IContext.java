/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B (unchecked) contexts.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 */
public interface IContext extends IRodinFile {

	public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".context"; //$NON-NLS-1$

	/**
	 * Returns a handle to the checked version of this context, that is the file
	 * produced when statically checking this context.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the checked version of this context
	 */
	public ISCContext getSCContext();

	/**
	 * Returns a handle to the file containing proof obligations for this
	 * context.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the PO file of this context
	 */
	public IPOFile getPOFile();

	/**
	 * Returns a handle to the file containing proofs for this context.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the proof file of this context
	 */
	public IPRFile getPRFile();

	public ICarrierSet[] getCarrierSets() throws RodinDBException;
	public IConstant[] getConstants() throws RodinDBException;
	public IAxiom[] getAxioms() throws RodinDBException;
	public ITheorem[] getTheorems() throws RodinDBException;
}
