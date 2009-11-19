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

import static org.eventb.core.EventBAttributes.GENERATED_ATTRIBUTE;
import static org.eventb.core.IConvergenceElement.Convergence.ANTICIPATED;
import static org.eventb.core.IConvergenceElement.Convergence.ORDINARY;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariable;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class Refines implements IObjectActionDelegate {

	private static final class CreateRefinement implements IWorkspaceRunnable {

		private final IMachineRoot abs;
		private final IMachineRoot con;

		public CreateRefinement(IMachineRoot abs, IMachineRoot con) {
			this.abs = abs;
			this.con = con;
		}

		public void run(IProgressMonitor monitor) throws RodinDBException {
			con.getRodinFile().create(false, monitor);
			con.setConfiguration(abs.getConfiguration(), null);
			createRefinesMachineClause(monitor);
			copyChildrenOfType(con, abs, ISeesContext.ELEMENT_TYPE, monitor);
			copyChildrenOfType(con, abs, IVariable.ELEMENT_TYPE, monitor);
			createEvents(monitor);
			removeGenerated(con, monitor);
			con.getRodinFile().save(null, false);
		}

		private void createRefinesMachineClause(IProgressMonitor monitor)
				throws RodinDBException {
			final IRefinesMachine refines = con.createChild(
					IRefinesMachine.ELEMENT_TYPE, null, monitor);
			refines.setAbstractMachineName(abs.getComponentName(), monitor);
		}

		private static <T extends IInternalElement> void copyChildrenOfType(
				IEventBRoot destination, IEventBRoot original,
				IInternalElementType<T> type, IProgressMonitor monitor)
				throws RodinDBException {

			final T[] elements = original.getChildrenOfType(type);
			if (elements.length == 0)
				return;
			final IEventBRoot[] containers = new IEventBRoot[] { destination };
			final IRodinDB rodinDB = destination.getRodinDB();
			rodinDB.copy(elements, containers, null, null, false, monitor);
		}

		private static void copyAttributes(IInternalElement destination,
				IInternalElement original, IProgressMonitor monitor)
				throws RodinDBException {
			for (IAttributeValue value : original.getAttributeValues()) {
				destination.setAttributeValue(value, monitor);
			}
		}
		
		private static void removeGenerated(IInternalElement element,
				IProgressMonitor monitor) throws RodinDBException {
			element.removeAttribute(GENERATED_ATTRIBUTE, monitor);
			final IRodinElement[] children = element.getChildren();
			for (IRodinElement child : children) {
				removeGenerated((IInternalElement) child, monitor);
			}
		}

		private void createEvents(IProgressMonitor monitor)
				throws RodinDBException {
			final IEvent[] absEvts = abs.getChildrenOfType(IEvent.ELEMENT_TYPE);
			for (IEvent absEvt : absEvts) {
				createEvent(absEvt, monitor);
			}
		}

		private void createEvent(IEvent absEvt, IProgressMonitor monitor)
				throws RodinDBException {
			final String name = absEvt.getElementName();
			final String label = absEvt.getLabel();
			final IEvent conEvt = con.getEvent(name);
			conEvt.create(null, monitor);
			copyAttributes(conEvt, absEvt, monitor);
			conEvt.setExtended(true, monitor);
			createRefinesEventClause(conEvt, label, monitor);
			setConvergence(conEvt, absEvt, monitor);
		}

		private void createRefinesEventClause(IEvent conEvt, String label,
				IProgressMonitor monitor) throws RodinDBException {
			if (!label.equals(IEvent.INITIALISATION)) {
				final IRefinesEvent refines = conEvt.createChild(
						IRefinesEvent.ELEMENT_TYPE, null, monitor);
				refines.setAbstractEventLabel(label, monitor);
			}
		}

		private void setConvergence(IEvent conEvt, IEvent absEvt,
				IProgressMonitor monitor) throws RodinDBException {
			final Convergence absCvg = absEvt.getConvergence();
			final Convergence conCvg = computeRefinementConvergence(absCvg);
			conEvt.setConvergence(conCvg, monitor);
		}

		private Convergence computeRefinementConvergence(Convergence absCvg) {
			switch (absCvg) {
			case ANTICIPATED:
				return ANTICIPATED;
			case CONVERGENT:
			case ORDINARY:
				return ORDINARY;
			}
			return ORDINARY;
		}

	}

	private IWorkbenchPart part;

	private ISelection selection;

	/**
	 * Constructor for Action1.
	 */
	public Refines() {
		super();
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		part = targetPart;
	}

	public void run(IAction action) {
		final IRodinFile abs = getSelectedMachine();
		if (abs == null) {
			return;
		}
		final IRodinFile con = askRefinementMachineFor(abs);
		if (con == null) {
			return;
		}
		final IMachineRoot absRoot = (IMachineRoot) abs.getRoot();
		final IMachineRoot conRoot = (IMachineRoot) con.getRoot();
		final CreateRefinement op = new CreateRefinement(absRoot, conRoot);
		try {
			RodinCore.run(op, null);
		} catch (RodinDBException e) {
			UIUtils.log(e, "When creating a refinement of abstract machine "
					+ absRoot.getComponentName() + " by concrete machine "
					+ conRoot.getComponentName());
			return;
		}
		UIUtils.linkToEventBEditor(con);
	}

	public void selectionChanged(IAction action, ISelection sel) {
		this.selection = sel;
	}

	/**
	 * Returns the selected machine if the selection is structured and contains
	 * exactly one element which is adaptable to a machine file. Otherwise,
	 * returns <code>null</code>.
	 * 
	 * @return the selected machine or <code>null</code>
	 */
	private IRodinFile getSelectedMachine() {
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() == 1) {
				return EventBPlugin.asMachineFile(ssel.getFirstElement());
			}
		}
		return null;
	}

	/**
	 * Asks the user the name of the concrete machine to create and returns it.
	 * 
	 * @param abs
	 *            the abstract machine to refine
	 * @return the concrete machine entered by the user or <code>null</code> if
	 *         canceled.
	 */
	private IRodinFile askRefinementMachineFor(IRodinFile abs) {
		final IRodinProject prj = abs.getRodinProject();
		final InputDialog dialog = new InputDialog(part.getSite().getShell(),
				"New REFINES Clause",
				"Please enter the name of the new machine", "m0",
				new RodinFileInputValidator(prj));
		dialog.open();

		final String name = dialog.getValue();
		if (name == null) {
			return null;
		}
		final String fileName = EventBPlugin.getMachineFileName(name);
		return prj.getRodinFile(fileName);
	}

}
