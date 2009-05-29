/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.model;

import java.util.List;

import org.rodinp.core.IRodinElement;

/**
 * Common protocol for clients that need to listen to changes in the event-B
 * explorer model.
 * 
 * @author Aurélien Gilles
 * 
 * @see ModelController#addListener(IModelListener)
 * @see ModelController#removeListener(IModelListener)
 */
public interface IModelListener {

	/**
	 * Indicates some Rodin elements have changed in the model. Client listener
	 * should take appropriate action to refresh itself.
	 * 
	 * @param toRefresh
	 *            list of Rodin elements that have changed
	 */
	public void refresh(List<IRodinElement> toRefresh);

}
