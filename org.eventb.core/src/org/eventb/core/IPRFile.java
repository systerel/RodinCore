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
 * Common protocol for Event-B Prover (PR) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @author Stefan Hallerstede
 * @author Farhad Mehta
 *
 */
public interface IPRFile extends IRodinFile {
	public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".prFile"; //$NON-NLS-1$
	
	public IPOPredicateSet getPredicateSet(String name) throws RodinDBException;
	public IPOIdentifier[] getIdentifiers() throws RodinDBException;
	public IPRSequent[] getSequents() throws RodinDBException;
	
}
