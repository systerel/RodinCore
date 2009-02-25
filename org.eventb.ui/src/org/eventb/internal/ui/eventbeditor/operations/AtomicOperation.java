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
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class AtomicOperation extends AbstractOperation {

	abstract class AbstractNavigation {

		IStatus status;

		abstract IStatus doRun(IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException;

		IStatus run(IProgressMonitor monitor, final IAdaptable info) {
			try {
				RodinCore.run(new IWorkspaceRunnable() {
					public void run(IProgressMonitor m) throws RodinDBException {
						try {
							status = doRun(m, info);
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}
				}, context.getRodinFile().getSchedulingRule(), monitor);
			} catch (RodinDBException e) {
				e.printStackTrace();
				return e.getStatus();
			}
			return status;
		}
	}

	protected final OperationTree operation;

	final RodinFileUndoContext context;

	private final AbstractNavigation execute = new AbstractNavigation() {
		@Override
		IStatus doRun(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return operation.execute(monitor, info);
		}

	};

	private final AbstractNavigation undo = new AbstractNavigation() {
		@Override
		IStatus doRun(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return operation.undo(monitor, info);
		}

	};

	private final AbstractNavigation redo = new AbstractNavigation() {
		@Override
		IStatus doRun(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return operation.redo(monitor, info);
		}

	};

	public AtomicOperation(RodinFileUndoContext context, OperationTree operation) {
		super(operation.getLabel());
		this.operation = operation;
		this.context = context;
		addContext(context);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, final IAdaptable info)
			throws ExecutionException {
		return execute.run(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, final IAdaptable info)
			throws ExecutionException {
		return redo.run(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, final IAdaptable info)
			throws ExecutionException {
		return undo.run(monitor, info);
	}

	public IInternalElement getCreatedElement() {
		return operation.getCreatedElement();
	}

	public Collection<IInternalElement> getCreatedElements() {
		return operation.getCreatedElements();
	}
}
