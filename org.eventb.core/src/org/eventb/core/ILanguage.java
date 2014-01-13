/*******************************************************************************
 * Copyright (c) 2013, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for representing a variant of the Event-B mathematical
 * language. This is implemented by storing explicitly a formula factory in the
 * Rodin database.
 * <p>
 * Clients should use the Proof Manager API rather than direct access to this
 * Rodin database API.
 * </p>
 * 
 * @author Laurent Voisin
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 3.0
 */
public interface ILanguage extends IInternalElement {

	IInternalElementType<ILanguage> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EventBPlugin.PLUGIN_ID + ".lang");

	/**
	 * Returns the formula factory stored in this element.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @return the formula factory implementing this language
	 * @throws CoreException
	 *             if there was a problem while deserializing
	 */
	FormulaFactory getFormulaFactory(IProgressMonitor monitor)
			throws CoreException;

	/**
	 * Stores a formula factory in this element.
	 * 
	 * @param factory
	 *            some formula factory
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the Rodin database
	 */
	void setFormulaFactory(FormulaFactory factory, IProgressMonitor monitor)
			throws RodinDBException;

}
