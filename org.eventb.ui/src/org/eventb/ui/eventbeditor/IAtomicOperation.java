/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.ui.eventbeditor;

import java.util.Collection;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Thomas Muller
 * @since 2.4
 */
public interface IAtomicOperation extends IUndoableOperation {

	void doExecute(IProgressMonitor monitor, final IAdaptable info)
			throws RodinDBException;

	void doUndo(IProgressMonitor monitor, final IAdaptable info)
			throws RodinDBException;

	void doRedo(IProgressMonitor monitor, final IAdaptable info)
			throws RodinDBException;

	IInternalElement getCreatedElement();
	
	Collection<IInternalElement> getCreatedElements(); 
	
}
