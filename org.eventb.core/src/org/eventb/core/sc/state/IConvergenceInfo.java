/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.eventb.core.IConvergenceElement;


/**
 * A convergence info provides information about the convergence of an event.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IAbstractEventInfo
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface IConvergenceInfo extends ISCState {
	
	/**
	 * Returns the convergence of the event that is represented by this convergence info.
	 * 
	 * @return the convergence of the event that is represented by this convergence info
	 */
	IConvergenceElement.Convergence getConvergence();
	
}
