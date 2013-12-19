/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofManager;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for references to expressions.
 * <p>
 * Clients should use the Proof Manager API rather than direct access to this
 * Rodin database API.
 * </p>
 *
 * @see IProofManager
 * 
 * @author Farhad Mehta
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IPRPredRef extends IInternalElement {

	IInternalElementType<IPRPredRef> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prPredRef"); //$NON-NLS-1$

	Predicate[] getPredicates(IProofStoreReader store) throws CoreException;

	void setPredicates(Predicate[] preds, IProofStoreCollector store,
			IProgressMonitor monitor) throws RodinDBException;
}
