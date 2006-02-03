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
 * Common protocol for events in Event-B statically checked (SC) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * Statically checked are similar to events @link org.eventb.core.IEvent
 * except that they contain statically checked variables @link org.eventb.core.ISCVariable
 * instead of variables @link org.eventb.core.IVariable.
 * </p>
 * @author Stefan Hallerstede
 *
 */
public interface ISCEvent extends IEvent {
	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".scEvent"; //$NON-NLS-1$
	
	ISCVariable[] getSCVariables() throws RodinDBException;

}
