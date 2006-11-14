/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * Common protocol for typed identifiers in Event-B Proof (PR) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @author Farhad Mehta
 *
 */
public interface IPRIdentifier extends ISCIdentifierElement {
	
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prIdentifier"); //$NON-NLS-1$

}
