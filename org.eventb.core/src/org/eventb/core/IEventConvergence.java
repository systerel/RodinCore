/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IEventConvergence extends IInternalElement {

	public static String CONVERGENCE_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".convergence";
	
	int ORDINARY = 0;
	int CONVERGENT = 1; 
	int ANTICIPATED = 2;
	
	void setConvergence(int value, IProgressMonitor monitor) throws RodinDBException;
	int getConvergence(IProgressMonitor monitor) throws RodinDBException;
}
