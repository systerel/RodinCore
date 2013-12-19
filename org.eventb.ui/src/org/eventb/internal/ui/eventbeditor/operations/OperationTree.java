/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.operations;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;

interface OperationTree {
	
	String getLabel();
	
	void doExecute(IProgressMonitor monitor, IAdaptable info)
			throws CoreException;

	void doRedo(IProgressMonitor monitor, IAdaptable info)
			throws CoreException;

	void doUndo(IProgressMonitor monitor, IAdaptable info)
			throws CoreException;

	void setParent(IInternalElement element) ;
	Collection<IInternalElement> getCreatedElements();
	IInternalElement getCreatedElement();
}
