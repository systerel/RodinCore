/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - updated Javadoc
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.seqprover.IHypAction;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for hypothesis management action in a persisted proof. The
 * predefined name attribute contains the hypothesis action type (select,
 * deselect, hide, show). The <code>org.eventb.core.prHyps</code> attribute
 * contains references to the predicates of the hypotheses manipulated by this
 * action.
 * <p>
 * Clients should use the Proof Manager API rather than direct access to this
 * Rodin database API.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IProofManager
 * 
 * @author Farhad Mehta
 * @since 1.0
 */
public interface IPRHypAction extends IInternalElement {

	IInternalElementType<IPRHypAction> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prHypAction"); //$NON-NLS-1$

	IHypAction getAction(IProofStoreReader store) throws RodinDBException;

	void setAction(IHypAction hypAction, IProofStoreCollector store,
			IProgressMonitor monitor) throws RodinDBException;
}
