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
 * 
 * @see IAbstractEventInfo
 * @see ICurrentEvent
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IConvergenceInfo extends ISCState {
	
	/**
	 * Returns the convergence of the event that is represented by this convergence info.
	 * 
	 * @return the convergence of the event that is represented by this convergence info
	 */
	IConvergenceElement.Convergence getConvergence();
	
}
