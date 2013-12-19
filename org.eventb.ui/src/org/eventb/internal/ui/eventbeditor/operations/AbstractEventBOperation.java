/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Common implementation for event-B undoable operations. It handles building
 * execution exceptions from Rodin database exceptions.
 * 
 * @author Laurent Voisin
 */
public abstract class AbstractEventBOperation extends AbstractOperation {

	public AbstractEventBOperation(String label) {
		super(label);
	}

	public abstract void doExecute(IProgressMonitor monitor,
			final IAdaptable info) throws CoreException;

	public abstract void doUndo(IProgressMonitor monitor, final IAdaptable info)
			throws CoreException;

	public abstract void doRedo(IProgressMonitor monitor, final IAdaptable info)
			throws CoreException;

	@Override
	public final IStatus execute(IProgressMonitor monitor, final IAdaptable info)
			throws ExecutionException {
		try {
			doExecute(monitor, info);
			return Status.OK_STATUS;
		} catch (CoreException e) {
			return wrapCoreException(e);
		}
	}

	@Override
	public final IStatus redo(IProgressMonitor monitor, final IAdaptable info)
			throws ExecutionException {
		try {
			doRedo(monitor, info);
			return Status.OK_STATUS;
		} catch (CoreException e) {
			return wrapCoreException(e);
		}
	}

	@Override
	public final IStatus undo(IProgressMonitor monitor, final IAdaptable info)
			throws ExecutionException {
		try {
			doUndo(monitor, info);
			return Status.OK_STATUS;
		} catch (CoreException e) {
			return wrapCoreException(e);
		}
	}

	private IStatus wrapCoreException(CoreException e)
			throws ExecutionException {
		final IStatus status = e.getStatus();
		// If cancellation, propagate the status
		if (status.matches(IStatus.CANCEL)) {
			return status;
		}
		throw new ExecutionException(
				"Internal error while executing operation " + getLabel(), e);
	}

}
