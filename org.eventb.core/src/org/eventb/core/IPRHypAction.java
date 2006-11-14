/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.seqprover.HypothesesManagement.Action;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 *
 */
public interface IPRHypAction extends IInternalElement {
	
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prHypAction"); //$NON-NLS-1$

	Action getAction(FormulaFactory factory, ITypeEnvironment typEnv, IProgressMonitor monitor) throws RodinDBException;
	void setAction(Action hypAction, IProgressMonitor monitor) throws RodinDBException;
}
