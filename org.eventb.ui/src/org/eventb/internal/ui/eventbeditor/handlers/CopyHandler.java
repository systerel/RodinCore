/*******************************************************************************
 * Copyright (c) 2007, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added the static method copyToClipboard
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ResourceTransfer;
import org.eventb.internal.ui.DragAndCopyUtil;
import org.eventb.internal.ui.RodinHandleTransfer;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

public class CopyHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		// Get the selection from the current active page.
		ISelection selection = EventBUIPlugin.getActivePage().getSelection();
		
		// If there is no selection then do nothing.
		if (selection == null)
			return "There is no selection";
		
		// If the selection is not a structured selection then do nothing.
		if (!(selection instanceof IStructuredSelection))
			return "The selection is not a structured selection";

		IStructuredSelection ssel = (IStructuredSelection) selection;

		Collection<IRodinElement> elements = new ArrayList<IRodinElement>();

		
		// Collect the list of Rodin Elements to be copy: If an ancester of
		// element is already selected, the element will not be added.
		for (Iterator<?> it = ssel.iterator(); it.hasNext();) {
			Object obj = it.next();
			IRodinElement element = null;
			if (obj instanceof IRodinElement) {
				element = (IRodinElement) obj;
			}
			if (element != null)
				elements = UIUtils.addToTreeSet(elements, element);
		}
		// Check if only Rodin projects are selected.
		boolean projSelected = DragAndCopyUtil
				.selectionIsOfTypeRodinProject(elements);
		
		// Check if only Rodin files are selected.
		boolean fileFoldersSelected = DragAndCopyUtil
				.selectionIsOfTypeRodinFile(elements);

		// Get the clipboard for the current workbench display.
		IWorkbench workbench = EventBUIPlugin.getDefault().getWorkbench();
		Clipboard clipboard = new Clipboard(workbench.getDisplay());
		
		// Copies projects into the clipboard
		// Copies as Resource, Text transfer
		// TODO Copy as Rodin handlers
		if (projSelected) {
			Collection<IResource> resources = new ArrayList<IResource>();
			StringBuffer buf = new StringBuffer();
			int i = 0;
			for (Iterator<?> it = ssel.iterator(); it.hasNext();) {
				IRodinProject prj = (IRodinProject) it.next();
				resources.add(prj.getResource());
				if (i > 0)
					buf.append("\n"); //$NON-NLS-1$
				buf.append(prj.getElementName());
				i++;
			}
			clipboard.setContents(new Object[] {
					resources.toArray(new IResource[resources.size()]),
					buf.toString() },
					new Transfer[] { ResourceTransfer.getInstance(),
							TextTransfer.getInstance() });
			return "Copy project(s) successfully";
		}

		// Copies files into the clipboard
		// Copies as Resource, File, Text transfer
		// TODO Copy as Rodin Handle?
		if (fileFoldersSelected) {
			Collection<IResource> resources = new ArrayList<IResource>();
			Collection<String> fileNames = new ArrayList<String>();
			int i = 0;
			StringBuffer buf = new StringBuffer();
			for (Iterator<?> it = ssel.iterator(); it.hasNext();) {
				IRodinFile file = (IRodinFile) it.next();
				IFile resource = file.getResource();
				resources.add(resource);
				IPath location = resource.getLocation();
				// location may be null. See bug 29491.
				if (location != null)
					fileNames.add(location.toOSString());
				if (i > 0)
					buf.append("\n"); //$NON-NLS-1$
				buf.append(file.getElementName());
				i++;
			}
				
			setClipboard(resources, fileNames, buf.toString());

			return "Copy file(s) successfully";
		}

		// Copies internal element
		// Copies as Rodin Handle & Text transfer
		copyToClipboard(elements, clipboard);

		return "Copy Rodin element successfully";
	}
	
	/**
	 * Copies both names and handles of the given elements in a clipboard build
	 * with the given display.
	 * 
	 * @param elements
	 *            the elements to copy
	 * @param clipboard
	 *            the clipboard to get the copied elements
	 */
	public static void copyToClipboard(
			final Collection<IRodinElement> elements, final Clipboard clipboard) {
		final StringBuffer buf = new StringBuffer();
		int i = 0;
		for (IRodinElement element : elements) {
			if (i > 0)
				buf.append("\n");
			buf.append(element);
			i++;
		}
		clipboard.setContents(
				new Object[] {
						elements.toArray(new IRodinElement[elements.size()]),
						buf.toString() },
				new Transfer[] { RodinHandleTransfer.getInstance(),
						TextTransfer.getInstance() });
	}

	/**
	 * Set the clipboard contents. Prompt to retry if clipboard is busy.
	 * 
	 * @param resources
	 *            the resources to copy to the clipboard
	 * @param fileNames
	 *            file names of the resources to copy to the clipboard
	 * @param names
	 *            string representation of all names
	 */
	private void setClipboard(Collection<IResource> resources,
			Collection<String> fileNames, String names) {
		IWorkbench workbench = EventBUIPlugin.getDefault().getWorkbench();
		Clipboard clipboard = new Clipboard(workbench.getDisplay());
		try {
			// if there are some file names then set the File transfer.
			if (fileNames.size() > 0) {
				clipboard
						.setContents(
								new Object[] {
										resources
												.toArray(new IResource[resources
														.size()]),
										fileNames.toArray(new String[fileNames
												.size()]), names },
						new Transfer[] { ResourceTransfer.getInstance(),
								FileTransfer.getInstance(),
								TextTransfer.getInstance() });
			} else { // Otherwise, set Resource and Text transfer only.
				clipboard.setContents(new Object[] { resources, names },
						new Transfer[] { ResourceTransfer.getInstance(),
								TextTransfer.getInstance() });
			}
		} catch (SWTError e) {
			if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD)
				throw e;
		}
	}

}
