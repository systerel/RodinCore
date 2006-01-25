/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public interface ISCMachine extends IMachine {
	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".scMachine"; //$NON-NLS-1$

	ISCCarrierSet[] getSCCarrierSets() throws RodinDBException;
	ISCConstant[] getSCConstants() throws RodinDBException;
	ISCVariable[] getSCVariables() throws RodinDBException;
	ISCEvent[] getSCEvents() throws RodinDBException;
	ISCAxiomSet[] getAxiomSets() throws RodinDBException;
	ISCTheoremSet[] getTheoremSets() throws RodinDBException;
	IAxiom[] getOldAxioms() throws RodinDBException;
	ITheorem[] getOldTheorems() throws RodinDBException;
	IInvariant[] getOldInvariants() throws RodinDBException;
}
