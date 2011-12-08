/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - exposed IAtomicOperation interface
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.operations;

import java.util.Collection;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.ui.eventbeditor.IAtomicOperation;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class AtomicOperation extends AbstractEventBOperation implements IAtomicOperation {

	abstract class AbstractNavigation {

		abstract void doRun(IProgressMonitor monitor, final IAdaptable info)
				throws RodinDBException;

		void run(IProgressMonitor monitor, final IAdaptable info)
				throws RodinDBException {
			RodinCore.run(new IWorkspaceRunnable() {
				@Override
				public void run(IProgressMonitor m) throws RodinDBException {
					doRun(m, info);
				}
			}, context.getRodinFile().getSchedulingRule(), monitor);
		}
	}

	protected final OperationTree operation;

	final RodinFileUndoContext context;

	private final AbstractNavigation execute = new AbstractNavigation() {
		@Override
		void doRun(IProgressMonitor monitor, IAdaptable info)
				throws RodinDBException {
			operation.doExecute(monitor, info);
		}
	};

	private final AbstractNavigation undo = new AbstractNavigation() {
		@Override
		void doRun(IProgressMonitor monitor, IAdaptable info)
				throws RodinDBException {
			operation.doUndo(monitor, info);
		}
	};

	private final AbstractNavigation redo = new AbstractNavigation() {
		@Override
		void doRun(IProgressMonitor monitor, IAdaptable info)
				throws RodinDBException {
			operation.doRedo(monitor, info);
		}
	};

	public AtomicOperation(RodinFileUndoContext context, OperationTree operation) {
		super(operation.getLabel());
		this.operation = operation;
		this.context = context;
		addContext(context);
	}

	@Override
	public void doExecute(IProgressMonitor monitor, final IAdaptable info)
			throws RodinDBException {
		execute.run(monitor, info);
	}

	@Override
	public void doRedo(IProgressMonitor monitor, final IAdaptable info)
			throws RodinDBException {
		redo.run(monitor, info);
	}

	@Override
	public void doUndo(IProgressMonitor monitor, final IAdaptable info)
			throws RodinDBException {
		undo.run(monitor, info);
	}

	@Override
	public IInternalElement getCreatedElement() {
		return operation.getCreatedElement();
	}

	@Override
	public Collection<IInternalElement> getCreatedElements() {
		return operation.getCreatedElements();
	}
}
