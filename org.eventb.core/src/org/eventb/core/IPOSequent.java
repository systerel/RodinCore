/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for proof obligations in Event-B Proof Obligation (PO) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * A sequent is a tuple (NAME, TYPE_ENV, HYP, GOAL, HINTS)
 * <ul>
 * <li>The name (NAME) identifies uniquely a sequent (resp. proof obligation) in a PO file.</li>
 * <li>The type environment (TYPE_ENV) specifies type of identifiers local to the sequent.
 * (The type environment is contained in the sequent in form of POIdentifiers.)</li>
 * <li>There is one hypothesis (HYP) in the sequent. It is of type POHypothesis.</li>
 * <li>There is one goal (GOAL) in the sequent. It is a POPredicate or a POModifiedPredicate.</li>
 * <li>There is one PODescription associated with the sequent.</li>
 * </ul>
 * </p>
 *
 * @author Stefan Hallerstede
 *
 */
public interface IPOSequent extends IInternalElement {
	public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".poSequent"; //$NON-NLS-1$
	
	public String getName();
	public IPOIdentifier[] getIdentifiers() throws RodinDBException;
	public IPOHypothesis getHypothesis() throws RodinDBException;
	public IPOAnyPredicate getGoal() throws RodinDBException;
	public IPODescription getDescription() throws RodinDBException;
}
