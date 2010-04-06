/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
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

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Common protocol for Event-B Proof Obligation (PO) files.
 * <p>
 * A proof obligation file contains two kinds of elements:
 * <ul>
 * <li>Predicate sets contain hypotheses that are common to several proof
 * obligations.</li>
 * <li>Sequents are the proof obligations themselves.</li>
 * </ul>
 * <p>
 * <b>NOTE and WARNING</b>: Proof obligations files contain internal references
 * to handles, e.g., to link predicate sets into a tree structure. There are
 * different kinds of proof obligation files: plain files (extension:
 * <code>po</code>), temporary files (extension <code>po_tmp</code>).
 * Internal references within any of these files are always stored relative to
 * the plain files and automatically translated back and forth when they appear
 * in temporary files. The translation is done by means of
 * {@link InternalElement#getSimilarElement(org.rodinp.core.IRodinFile)}.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IPOPredicateSet
 * @see IPOSelectionHint
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 */
public interface IPORoot extends IEventBRoot, IPOStampedElement {

	public IInternalElementType<IPORoot> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EventBPlugin.PLUGIN_ID + ".poFile"); //$NON-NLS-1$

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
