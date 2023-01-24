/*******************************************************************************
 * Copyright (c) 2023 Université de Lorraine and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Université de Lorraine - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.handlers;

import static fr.systerel.internal.explorer.navigator.ExplorerUtils.getContextRootChildren;
import static fr.systerel.internal.explorer.navigator.ExplorerUtils.getMachineRootChildren;
import static fr.systerel.internal.explorer.navigator.ExplorerUtils.log;
import static org.eclipse.ui.handlers.HandlerUtil.getCurrentStructuredSelection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.model.ModelContext;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelMachine;

/**
 * Force refreshing of a model.
 *
 * @author Guillaume Verdier
 */
public class RefreshHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = getCurrentStructuredSelection(event);
		ModelController controller = ModelController.getInstance();
		if (selection.isEmpty()) {
			try {
				selection = new StructuredSelection(RodinCore.getRodinDB().getRodinProjects());
			} catch (RodinDBException e) {
				log(e, "error while refreshing explorer");
				return null;
			}
		}
		for (Object element : selection) {
			if (element instanceof IRodinElement) {
				controller.refreshModel((IRodinElement) element);
				// It appears that for contexts and machines, refreshModel()
				// sets a flag to indicate that PO/PS have to be refreshed, but
				// it is not done synchronously. We force it here. For projects,
				// we have to process all children contexts/machines.
				if (element instanceof IContextRoot) {
					forceProcess((IContextRoot) element);
				} else if (element instanceof IMachineRoot) {
					forceProcess((IMachineRoot) element);
				} else if (element instanceof IRodinProject) {
					var project = (IRodinProject) element;
					try {
						for (IContextRoot context : getContextRootChildren(project)) {
							forceProcess(context);
						}
					} catch (RodinDBException e) {
						// Log the error and try to refresh remaining objects
						log(e, "error while refreshing contexts of " + project.getElementName());
					}
					try {
						for (IMachineRoot machine : getMachineRootChildren(project)) {
							forceProcess(machine);
						}
					} catch (RodinDBException e) {
						// Log the error and try to refresh remaining objects
						log(e, "error while refreshing machines of " + project.getElementName());
					}
				}
			}
		}
		return null;
	}

	private void forceProcess(IContextRoot context) {
		ModelContext model = ModelController.getContext(context);
		model.processPORoot();
		model.processPSRoot();
	}

	private void forceProcess(IMachineRoot machine) {
		ModelMachine model = ModelController.getMachine(machine);
		model.processPORoot();
		model.processPSRoot();
	}

}
