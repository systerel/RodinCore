/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.actions;

import java.util.Collection;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public interface OperationTree {
	
	String getLabel();
	
	void doExecute(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException;

	void doRedo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException;

	void doUndo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException;

	void setParent(IInternalElement element) ;
	
	Collection<IInternalElement> getCreatedElements();
	
	IInternalElement getCreatedElement();
	
}
