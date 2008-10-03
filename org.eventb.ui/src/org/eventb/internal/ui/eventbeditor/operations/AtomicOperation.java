/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class AtomicOperation extends AbstractOperation {

	protected final OperationTree operation ;
	
	public AtomicOperation(OperationTree command) {
		super("AtomicCommand");
		this.operation = command ;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, final IAdaptable info)
			throws ExecutionException {
		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor m) throws RodinDBException {
					try {
						operation.execute(m, info) ;
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				}, monitor);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Status.OK_STATUS;
	}


	@Override
	public IStatus redo(IProgressMonitor monitor, final IAdaptable info)
			throws ExecutionException {
		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor m) throws RodinDBException {
					try {
						operation.redo(m, info) ;
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
				}, monitor);
			return Status.OK_STATUS;
		} catch (RodinDBException e) {
			return e.getStatus();
		}
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, final IAdaptable info)
			throws ExecutionException {
		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor m) throws RodinDBException {
					try {
						operation.undo(m, info) ;
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				}, monitor);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		return Status.OK_STATUS;
	}
	
	public IInternalElement getCreatedElement(){
		return operation.getCreatedElement();
	}
	public Collection<IInternalElement> getCreatedElements(){
		return operation.getCreatedElements();
	}

}
