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
 * Common protocol for carrier sets in Event-B statically checked (SC) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * Statically checked carrier sets have a name <code>getName()</code>
 * and a type <code>getType()</code>.
 * </p>
 * @author Stefan Hallerstede
 *
 */
public interface ISCCarrierSet extends ICarrierSet {

	public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".scCarrierSet"; //$NON-NLS-1$
	
	public String getName();
	public String getType() throws RodinDBException;

}
