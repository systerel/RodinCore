/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eventb.core.seqprover.HypothesesManagement.Action;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 *
 */
public interface IPRHypAction extends IInternalElement {
	public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".prHypAction"; //$NON-NLS-1$

	public Action getAction() throws RodinDBException;
	public void setAction(Action hypAction) throws RodinDBException;
}
