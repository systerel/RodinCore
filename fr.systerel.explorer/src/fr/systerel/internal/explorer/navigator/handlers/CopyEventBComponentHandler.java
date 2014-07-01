/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ResourceTransfer;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPRRoot;
import org.rodinp.core.IRodinElement;

/**
 * Handler to copy EventB components to the clipboard
 * 
 * @author Josselin Dolhen
 */
public class CopyEventBComponentHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection currentSelection = HandlerUtil
				.getCurrentSelectionChecked(event);
		if (!(currentSelection instanceof IStructuredSelection)) {
			return null;
		}
		final IStructuredSelection selection = (IStructuredSelection) currentSelection;

		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Clipboard clipboard = new Clipboard(workbench.getDisplay());

		final Iterator<?> iterator = selection.iterator();
		final Collection<IResource> resources = new ArrayList<IResource>();

		while (iterator.hasNext()) {
			final Object selected = iterator.next();
			if (!(selected instanceof IEventBRoot)) {
				continue;
			}

			final IEventBRoot selectedRoot = (IEventBRoot) selected;
			final IRodinElement rodinElem = selectedRoot.getParent();
			final IPRRoot prRoot = selectedRoot.getPRRoot();
			if (prRoot.exists()) {
				resources.add(prRoot.getResource());
			}
			resources.add(rodinElem.getResource());

		}

		clipboard.setContents(new Object[] { resources
				.toArray(new IResource[resources.size()]), },
				new Transfer[] { ResourceTransfer.getInstance(), });

		return null;
	}

}
