/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.rodinp.core.IInternalElement;

/**
 * Common protocol for Event-B actions.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @author Laurent Voisin
 */
public interface IAction extends IInternalElement {

	public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".action";
	// No additional methods
}
