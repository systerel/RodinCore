/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.basis.PRReasoner;
import org.eventb.core.seqprover.IReasonerDesc;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for reasoners used in a proof.
 * <p>
 * Clients should use the Proof Manager API rather than direct access to this
 * Rodin database API.
 * </p>
 * 
 * @author Nicolas Beauger
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 2.2
 * 
 */
public interface IPRReasoner extends IInternalElement {

	IInternalElementType<PRReasoner> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prReas");

	/**
	 * Returns the descriptor of this stored reasoner.
	 * 
	 * @return a reasoner descriptor
	 * @throws RodinDBException
	 */
	IReasonerDesc getReasoner() throws RodinDBException;

	/**
	 * Sets the descriptor of this stored reasoner.
	 * 
	 * @param reasoner
	 *            a reasoner descriptor
	 * @param monitor
	 *            a progress monitor
	 * @throws RodinDBException
	 */
	void setReasoner(IReasonerDesc reasoner, IProgressMonitor monitor)
			throws RodinDBException;
}