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
 * Common protocol for typed identifiers in Event-B Proof Obligation (PO) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * A type expression is a pair (NAME, EXPR).
 * It defines a type with name NAME <code>getName</code> and 
 * described by expression EXPR <code>getType()</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 *
 */
public interface IPOIdentifier extends IInternalElement, ISCIdentifierElement {
	public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".poIdentifier"; //$NON-NLS-1$

	public String getName();	
	public String getType() throws RodinDBException;
}
