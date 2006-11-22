/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B Proof Obligation (PO) files.
 * <p>
 * A proof obligation file contains two kinds of elements:
 * <ul>
 * <li>Predicate sets contain hypotheses that are common to several
 * proof obligations.</li>
 * <li>Sequents are the proof obligations themselves.</li>
 * </ul>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
public interface IPOFile extends IRodinFile {

	public IFileElementType ELEMENT_TYPE = RodinCore
			.getFileElementType(EventBPlugin.PLUGIN_ID + ".poFile"); //$NON-NLS-1$

	/**
	 * Returns a handle to the checked version of the context for which this
	 * proof obligation file has been generated.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the checked version of the corresponding context
	 */
	public ISCContextFile getSCContext();

	/**
	 * Returns a handle to the checked version of the machine for which this
	 * proof obligation file has been generated.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the checked version of the corresponding machine
	 */
	public ISCMachineFile getSCMachine();

	/**
	 * Returns a handle to the file containing proofs for this component.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the proof file of this component
	 */
	public IPRFile getPRFile();

	/**
	 * Returns a handle to the file containing proof status for this component.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the proof file of this component
	 */
	public IPSFile getPSFile();

	/**
	 * Returns a handle to a child predicate set with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the predicate set
	 * @return a handle to a child predicate set with the given element name
	 */
	public IPOPredicateSet getPredicateSet(String elementName);

	/**
	 * Returns the predicate sets of this file.
	 * 
	 * @return an array of all predicate sets in this file
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	public IPOPredicateSet[] getPredicateSets() throws RodinDBException;

	/**
	 * Returns handles to the proof obligations of this component.
	 * 
	 * @return an array of all proof obligations in this file
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	public IPOSequent[] getSequents() throws RodinDBException;

	/**
	 * Returns a handle to a child sequent with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the sequent
	 * @return a handle to a child sequent with the given element name
	 */
	public IPOSequent getSequent(String elementName);

}
