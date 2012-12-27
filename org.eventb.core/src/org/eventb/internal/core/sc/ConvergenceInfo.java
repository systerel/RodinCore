/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.sc;

import org.eventb.core.IConvergenceElement;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.core.sc.state.IConvergenceInfo;
import org.eventb.internal.core.tool.state.State;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ConvergenceInfo extends State implements IConvergenceInfo {

	private final IConvergenceElement.Convergence convergence;
	
	ConvergenceInfo(IConvergenceElement.Convergence convergence) throws RodinDBException {
		this.convergence = convergence;
	}
	
	@Override
	public Convergence getConvergence() {
		return convergence;
	}


}
