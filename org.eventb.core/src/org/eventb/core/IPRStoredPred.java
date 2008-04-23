/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - updated Javadoc
 ******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofManager;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for proof predicates.
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
 */
public interface IPRStoredPred extends IInternalElement {

	IInternalElementType<IPRStoredPred> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prPred"); //$NON-NLS-1$

	Predicate getPredicate(FormulaFactory factory, ITypeEnvironment typEnv)
			throws RodinDBException;

	void setPredicate(Predicate predicate, IProgressMonitor monitor)
			throws RodinDBException;

	// extra free idents
	FreeIdentifier[] getFreeIdents(FormulaFactory factory)
			throws RodinDBException;

	void setFreeIdents(FreeIdentifier[] freeIdents, IProgressMonitor monitor)
			throws RodinDBException;
}
