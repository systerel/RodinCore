/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - fully rewritten the run() method
 *******************************************************************************/
package org.eventb.internal.ui.projectexplorer.actions;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextFile;
import org.eventb.core.IExtendsContext;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class Extends implements IObjectActionDelegate {

	private static final class CreateRefinement implements IWorkspaceRunnable {

		private final IContextFile abs;
		private final IContextFile con;

		public CreateRefinement(IContextFile abs, IContextFile con) {
			this.abs = abs;
			this.con = con;
		}

		public void run(IProgressMonitor monitor) throws RodinDBException {
			con.create(false, monitor);
			con.setConfiguration(abs.getConfiguration(), null);
			createExtendsContextClause(monitor);
			con.save(null, false);
		}

		private void createExtendsContextClause(IProgressMonitor monitor)
				throws RodinDBException {
			final IExtendsContext refines = con
					.getExtendsClause("internal_extendsContext1");
			refines.create(null, monitor);
			refines.setAbstractContextName(abs.getComponentName(), monitor);
		}

	}
	
	private IWorkbenchPart part;

	private ISelection selection;
	
	public void run(IAction action) {
		final IContextFile abs = getSelectedContext();
		if (abs == null) {
			return;
		}
		final IContextFile con = askRefinementContextFor(abs);
		if (con == null) {
			return;
		}
		final CreateRefinement op = new CreateRefinement(abs, con);
		try {
			RodinCore.run(op, null);
		} catch (RodinDBException e) {
			// TODO report error to end user
			e.printStackTrace();
			return;
		}
		UIUtils.linkToEventBEditor(con);
	}

	public void selectionChanged(IAction action, ISelection sel) {
		this.selection = sel;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		part = targetPart;
	}

	/**
	 * Returns the selected machine if the selection is structured and contains
	 * exactly one element which is adaptable to a machine file. Otherwise,
	 * returns <code>null</code>.
	 * 
	 * @return the selected machine or <code>null</code>
	 */
	private IContextFile getSelectedContext() {
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() == 1) {
				return EventBPlugin.asContextFile(ssel.getFirstElement());
			}
		}
		return null;
	}

	/**
	 * Asks the user the name of the concrete machine to create and returns it.
	 * 
	 * @param abs
	 *            the abstract machine to refine
	 * @return the concrete machine entered by the user or <code>null</code>
	 *         if canceled.
	 */
	private IContextFile askRefinementContextFor(IContextFile abs) {
		final IRodinProject prj = abs.getRodinProject();
		final InputDialog dialog = new InputDialog(part.getSite().getShell(),
				"New EXTENDS Clause",
				"Please enter the name of the new context", "c0",
				new RodinFileInputValidator(prj));
		dialog.open();

		final String name = dialog.getValue();
		if (name == null) {
			return null;
		}
		final String fileName = EventBPlugin.getContextFileName(name);
		return (IContextFile) prj.getRodinFile(fileName);
	}

}
