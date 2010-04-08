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
 *     Systerel - separation of file and root element
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
import org.eventb.core.IContextRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class Extends implements IObjectActionDelegate {

	private static final class CreateRefinement implements IWorkspaceRunnable {

		private final IContextRoot abs;
		private final IContextRoot con;

		public CreateRefinement(IContextRoot abs, IContextRoot con) {
			this.abs = abs;
			this.con = con;
		}

		public void run(IProgressMonitor monitor) throws RodinDBException {
			con.getRodinFile().create(false, monitor);
			con.setConfiguration(abs.getConfiguration(), null);
			createExtendsContextClause(monitor);
			con.getRodinFile().save(null, false);
		}

		private void createExtendsContextClause(IProgressMonitor monitor)
				throws RodinDBException {
			final IExtendsContext refines = con.createChild(
					IExtendsContext.ELEMENT_TYPE, null, monitor);
			refines.setAbstractContextName(abs.getComponentName(), monitor);
		}

	}
	
	private IWorkbenchPart part;

	private ISelection selection;
	
	public void run(IAction action) {
		final IRodinFile abs = getSelectedContext();
		final IContextRoot absRoot = (IContextRoot) abs.getRoot();
		if (abs == null) {
			return;
		}
		final IRodinFile con = askRefinementContextFor(abs);
		final IContextRoot conRoot = (IContextRoot) con.getRoot();
		if (con == null) {
			return;
		}
		final CreateRefinement op = new CreateRefinement(absRoot, conRoot);
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
	 * Returns the selected context if the selection is structured and contains
	 * exactly one element which is adaptable to a context file. Otherwise,
	 * returns <code>null</code>.
	 * 
	 * @return the selected context or <code>null</code>
	 */
	private IRodinFile getSelectedContext() {
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
	private IRodinFile askRefinementContextFor(IRodinFile abs) {
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
		return prj.getRodinFile(fileName);
	}

}
